#!/usr/bin/env python3

# -*- coding: utf-8 -*-
import inspect
import os
import random
import string
import sys
import traceback
from enum import Enum

from jsonschema import validate

from fakesession import FakeSession
from jsonresponse import JsonResp

""" <config> """
# SERVICE INFO
PORT = 8080

# DEBUG -- logs to stderr, TRACE -- verbose log
DEBUG = os.getenv("DEBUG", True)
TRACE = os.getenv("TRACE", False)
""" </config> """

JOBS_NAMES = open(os.path.join(os.path.abspath(
    os.path.dirname(__file__)), "jobs_names.txt")).read().split('\n')
JOBS_TITLES = open(os.path.join(os.path.abspath(
    os.path.dirname(__file__)), "jobs_titles.txt")).read().split('\n')
REPORTS_TITLES = open(os.path.join(os.path.abspath(
    os.path.dirname(__file__)), "reports_titles.txt")).read().split('\n')
ROLES = ["EMPLOYEE", "EMPLOYER"]

JSONResp = JsonResp()


def rand_string(len=12):
    alphabet = string.ascii_uppercase + string.ascii_lowercase + string.digits
    return "".join(random.choice(alphabet) for _ in range(len))


def info():
    print('vulns: 1:2', flush=True, end="")
    exit(101)


def check(host):
    _log(f"Running CHECK on {host}")
    s_employer = FakeSession(host, PORT)
    username = rand_string()
    password = rand_string()
    description = rand_string()

    _log("Check register")
    _register(s_employer, username, password, description, "EMPLOYER")

    _log("Check login")
    token = _login(s_employer, username, password)
    s_employer.headers = {"Authorization": f"Bearer {token}"}

    _log("Check get user")
    _get_user(s_employer)

    _log("Check get all jobs")
    _get_all_job(s_employer)

    _log("Check create job")
    job = _create_job(s_employer, "TEST")
    job_id = job["id"]

    s_employee = FakeSession(host, PORT)
    username = rand_string()
    password = rand_string()
    description = rand_string()

    _log("Check register")
    _register(s_employee, username, password, description, "EMPLOYEE")

    _log("Check login")
    token = _login(s_employee, username, password)
    s_employee.headers = {"Authorization": f"Bearer {token}"}

    _log("Check join to job")
    _join_job(s_employee, job_id)

    _log("Check select user in job")
    _select_user(s_employer, job_id, username)

    _log("Check get job and employee")
    response = _get_job(s_employee, job_id)
    if response["employee"] != username:
        die(ExitStatus.MUMBLE, f"Username didnt select to job in {response}")

    die(ExitStatus.OK, "Check END")


def put(host, flag_id, flag, vuln):
    _log(f"Running PUT on {host} with {flag_id}:{flag}")

    s = FakeSession(host, PORT)
    username = rand_string()
    password = rand_string()
    description = rand_string()

    if vuln == "1":
        _register(s, username, password, description, "EMPLOYER")
        token = _login(s, username, password)
        s.headers = {"Authorization": f"Bearer {token}"}

        job = _create_job(s, flag)
        job_id = job["id"]
        print(f"{token};{job_id}", file=sys.stdout, flush=True)
    elif vuln == "2":
        _register(s, username, password, description, "EMPLOYEE")
        token = _login(s, username, password)
        s.headers = {"Authorization": f"Bearer {token}"}

        user_id = _create_report(s, host, flag)
        print(f"{token};{user_id}", file=sys.stdout, flush=True)
    die(ExitStatus.OK, "Put END")


def get(host, flag_id, flag, vuln):
    _log(f"Running GET on {host} with {flag_id}:{flag}")

    s = FakeSession(host, PORT)

    if vuln == "1":
        data = flag_id.split(";")
        token = data[0]
        job_id = data[1]

        s.headers = {"Authorization": f"Bearer {token}"}
        _log("Check flag in Job")
        response = _get_job(s, job_id)["description"]
        if flag not in response:
            die(ExitStatus.CORRUPT, f"Can't find a flag in {response}")
    elif vuln == "2":
        data = flag_id.split(";")
        token = data[0]
        user_id = data[1]

        s.headers = {"Authorization": f"Bearer {token}"}
        _log("Check flag in Reports")
        response = _get_all_report(s, user_id)
        if flag not in response:
            die(ExitStatus.CORRUPT, f"Can't find a flag in {response}")
    die(ExitStatus.OK, "Get END")


def _register(s, username, password, description, role):
    data = {
        "username": username,
        "password": password,
        "description": description,
        "role": role
    }

    try:
        r = s.post("/register", json=data)
    except Exception as e:
        die(ExitStatus.DOWN, f"[_register] Failed to register in service: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE,
            f"[_register] Unexpected /register code: {r.status_code}")


def _login(s, username, password):
    data = {
        "username": username,
        "password": password,
    }

    try:
        r = s.post("/login", json=data)
    except Exception as e:
        die(ExitStatus.DOWN, f"[_login] Failed to login in service: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE, f"[_login] Unexpected /login code {r.status_code}")

    return r.json()["token"]


def _get_user(s):
    try:
        r = s.get("/user")
    except Exception as e:
        die(ExitStatus.DOWN, f"[_get_user] Failed to login in service: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE, f"[_get_user] Unexpected /user code {r.status_code} {r.text}")

    return r.json()


def _create_job(s, description):
    data = {
        "name": random.choice(JOBS_NAMES),
        "title": random.choice(JOBS_TITLES),
        "description": description
    }

    try:
        r = s.post("/jobs", json=data)
    except Exception as e:
        die(ExitStatus.DOWN, f"[_create_job] Failed to create job: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE, f"[_create_job] Unexpected /jobs code {r.status_code}")

    try:
        validate(instance=r.json(), schema=JSONResp.job)
    except Exception as e:
        die(ExitStatus.MUMBLE, f"[_create_job] Incorrect response format:\n{e}")

    return r.json()


def _get_job(s, job_id):
    try:
        r = s.get(f"/jobs/{job_id}")
    except Exception as e:
        die(ExitStatus.DOWN, f"[_get_job] Failed to get job: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE,
            f"[_get_job] Unexpected /jobs/{job_id} status code: {r.status_code}")
    try:
        validate(instance=r.json(), schema=JSONResp.job)
    except Exception as e:
        die(ExitStatus.MUMBLE, f"[_get_job] Incorrect response format:\n{e}")
    return r.json()


def _get_all_job(s):
    try:
        r = s.get(f"/jobs")
    except Exception as e:
        die(ExitStatus.DOWN, f"[_get_all_job(s)] Failed to get all jobs: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE,
            f"[_get_all_job(s)] Unexpected /jobs status code: {r.status_code}")
    try:
        validate(instance=r.json(), schema=JSONResp.all_jobs)
    except Exception as e:
        die(ExitStatus.MUMBLE, f"[_get_all_job(s)] Incorrect response format:\n{e}")
    return r.json()


def _select_user(s, job_id, username):
    try:
        r = s.put(f"/jobs/{job_id}/select/{username}")
    except Exception as e:
        die(ExitStatus.DOWN, f"[_select_user(s, job_ID, username)] Failed to select user on job: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE, f"[_select_user(s, job_ID, username)] Failed to select user on job: {r.status_code}")


def _join_job(s, job_id):
    try:
        r = s.put(f"/jobs/{job_id}/join")
    except Exception as e:
        die(ExitStatus.DOWN, f"[_join_job(s)] Failed to join job: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE, f"[_join_job(s)] Failed to join job: {r.status_code}")


def _create_report(s, host, description):
    user = _get_user(s)
    job = list(reversed(_get_all_job(s)))[0]
    job_id = job["id"]
    _join_job(s, job_id)

    url = f"http://{host}:8080/jobs/{job_id}"
    username = user["username"]
    user_id = user["id"]

    query = {
        "query": "mutation {"
                 "addReport(input: {"
                 "title: " + "\"" + random.choice(REPORTS_TITLES) + "\"" +
                 " description: " + "\"" + description + "\"" +
                 " url: " + "\"" + url + "\"" +
                 " user: " + "\"" + username + "\"" +
                 " userId: " + "\"" + user_id + "\"}){title description user userId}}"
    }

    try:
        r = s.post("/report", json=query)
    except Exception as e:
        die(ExitStatus.DOWN, f"[_create_report] Failed to create report: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE, f"[_create_report] Unexpected /report code {r.status_code}")

    return user_id


def _get_all_report(s, user_id):
    query = {
        "query": "{reports(userId: \"" + user_id + "\"){title description url user userId}}"
    }

    try:
        r = s.get("/report", json=query)
    except Exception as e:
        die(ExitStatus.DOWN, f"[_get_all_report] Failed get all reports: {e}")

    if r.status_code != 200:
        die(ExitStatus.MUMBLE, f"[_get_all_report] Unexpected /report code {r.status_code}")
    try:
        validate(instance=r.json(), schema=JSONResp.all_reports)
    except Exception as e:
        die(ExitStatus.MUMBLE, f"[_get_all_report] Incorrect response format:\n{e}")

    return r.text


""" <common> """


class ExitStatus(Enum):
    OK = 101
    CORRUPT = 102
    MUMBLE = 103
    DOWN = 104
    CHECKER_ERROR = 110


def _log(obj):
    if DEBUG and obj:
        caller = inspect.stack()[1].function
        print(f"[{caller}] {obj}", file=sys.stderr, flush=True)
    return obj


def die(code: ExitStatus, msg: str):
    if msg:
        print(msg, file=sys.stderr, flush=True)
    exit(code.value)


def _main():
    action, *args = sys.argv[1:]

    try:
        if action == "info":
            info()
        elif action == "check":
            host, = args
            check(host)
        elif action == "put":
            host, flag_id, flag, vuln = args
            put(host, flag_id, flag, vuln)
        elif action == "get":
            host, flag_id, flag, vuln = args
            get(host, flag_id, flag, vuln)
        else:
            raise IndexError
    except IndexError:
        die(ExitStatus.CHECKER_ERROR, f"Usage: {sys.argv[0]} check|put|get IP FLAGID FLAG")
    except Exception as e:
        die(ExitStatus.CHECKER_ERROR, f"Exception: {e}. Stack:\n {traceback.format_exc()}")


""" </common> """

if __name__ == "__main__":
    _main()
