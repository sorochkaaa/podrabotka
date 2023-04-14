#!/usr/bin/env python3

import requests
import re
import sys
import socket
import random
import string

#IP = sys.argv[1]
PORT = 8080
#hosts = ["192.168.100.3", "192.168.100.4", "192.168.100.5", "192.168.100.6", "192.168.100.8"]
hosts = ["192.168.100.8"]

def random_string():
    return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(10))

try:
    username_employee = random_string()
    password_employee = random_string()
    username_employer = random_string()
    password_employer = random_string()
    for host in hosts:
        s = requests.Session()
        data = {
            "username":username_employee,
            "password":password_employee,
            "description":"", 
            "role":"EMPLOYEE"
        }
        r = s.post(f'http://{host}:{PORT}/register', json=data)
        data = {
            "username":username_employee,
            "password":password_employee
        }
        r = s.post(f'http://{host}:{PORT}/login', json=data)
        headers_employee = {
            'Authorization':f'Bearer {r.json()["token"]}',
        }
        data = {
            "username":password_employer,
            "password":password_employer, 
            "description":"",
            "role":"EMPLOYER"
        }
        r = s.post(f'http://{host}:{PORT}/register', json=data)
        data = {
            "username":password_employer,
            "password":password_employer
        }
        r = s.post(f'http://{host}:{PORT}/login', json=data)
        headers_employer = {
            'Authorization':f'Bearer {r.json()["token"]}',
        }
        r = s.get(f'http://{host}:{PORT}/jobs', headers=headers_employee)
        lastJobs = [*r.json()[-10:]]
        jobsID = [lastJobs[i]["id"] for i in range(10)]
        print(jobsID)
        for i in range(10):
            r = s.put(f'http://{host}:{PORT}/jobs/{jobsID[i]}/join', headers=headers_employee)
            r = s.put(f'http://{host}:{PORT}/jobs/{jobsID[i]}/select/{username_employee}', headers=headers_employer)
            r = s.get(f'http://{host}:{PORT}/jobs/{jobsID[i]}', headers=headers_employee)
            print(r.json()["description"])
except Exception as e:
    print(e)