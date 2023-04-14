class JsonResp():

    def __init__(self):
        self.user = {
            "type": "object",
            "properties": {
                "id": {"type": "string"},
                "username": {"type": "string"},
                "password": {"type": "string"},
                "description": {"type": "string"},
                "role": {"type": "string"}
            },
            "required": ["username", "password", "id", "description", "role"]
        }
        self.job = {
            "type": "object",
            "properties": {
                "id": {"type": "string"},
                "name": {"type": "string"},
                "title": {"type": "string"},
                "description": {"type": "string"},
                "employee": {"type": "string"},
                "users": {"type": "array", "items": {"type": "string"}}
            },
            "required": ["id", "name", "title", "description", "employee", "users"]
        }
        self.all_jobs = {
            "type": "array",
            "items": {"type": "object", "properties": {
                "id": {"type": "string"},
                "name": {"type": "string"},
                "title": {"type": "string"},
                "description": {"type": "string"},
                "employee": {"type": "string"},
                "users": {"type": "array", "items": {"type": "string"}}
            }},
            "required": ["id", "name", "title", "description", "employee", "users"]
        }
        self.all_reports = {
            "type": "object",
            "properties": {
                "data": {
                    "type": "object",
                    "properties": {
                        "reports": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "title": {"type": "string"},
                                    "description": {"type": "string"},
                                    "url": {"type": "string"},
                                    "user": {"type": "string"},
                                    "userId": {"type": "string"},
                                }
                            }
                        }
                    }
                }
            }
        }
