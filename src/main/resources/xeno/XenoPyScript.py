from XenoNLPRequest import xeno_nlper

import os
import sys
import json

xeno_input = sys.argv[1]  # 从脚本获取原文

xeno_response, xeno_status = xeno_nlper(xeno_input)  # 获取语言理解结果及状态码
# print("拿到了：" + json.dumps(xeno_response) + "\n")
if xeno_status == 200:
    xeno_intent = xeno_response["intent"]["name"]
    xeno_text = xeno_response["text"]
    xeno_entities = xeno_response["entities"]
    xeno_output = ""

    if xeno_intent == "app_msg":
        xeno_output = {
            "orderType": "AppMsg",
            "title": "",
            "content": "",
            "object": []
        }
        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "title":
                xeno_output["title"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]

    elif xeno_intent == "txt_msg":
        xeno_output = {
            "orderType": "TxtMsg",
            "content": "",
            "object": []
        }
        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]

    elif xeno_intent == "pic_msg":
        xeno_output = {
            "orderType": "PicMsg",
            "image": "",
            "object": []
        }
        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "image":
                xeno_output["image"] += xeno_entity["value"]

    elif xeno_intent == "link_msg":
        xeno_output = {
            "orderType": "LinkMsg",
            "title": "",
            "content": "",
            "url": "",
            "object": []
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "url":
                xeno_output["url"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "title":
                xeno_output["title"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]

    elif xeno_intent == "mul_msg":
        xeno_output = {
            "orderType": "MulMsg",
            "title": "",
            "content": "",
            "image": "",
            "url": "",
            "object": []
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "url":
                xeno_output["url"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "title":
                xeno_output["title"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "image":
                xeno_output["image"] += xeno_entity["value"]

    elif xeno_intent == "id_msg":
        xeno_output = {
            "orderType": "IDMsg",
            "title": "",
            "content": "",
            "object": []
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "url":
                xeno_output["url"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "title":
                xeno_output["title"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "image":
                xeno_output["image"] += xeno_entity["value"]

    elif xeno_intent == "sys_msg":
        xeno_output = {
            "orderType": "SysMsg",
            "title": "",
            "content": "",
            "desc": [],
            "object": [],
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "desc":
                xeno_output["desc"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "title":
                xeno_output["title"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]

    elif xeno_intent == "oa_msg":
        xeno_output = {
            "orderType": "OAMsg"
        }

    elif xeno_intent == "add_man":
        xeno_output = {
            "orderType": "AddMan",
            "name": "",
            "mobile": "",
            "dept": "",
            "job": "",
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "name":
                xeno_output["name"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "mobile":
                xeno_output["mobile"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "dept":
                xeno_output["dept"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "job":
                xeno_output["job"] += xeno_entity["value"]

    elif xeno_intent == "del_man":
        xeno_output = {
            "orderType": "DelMan",
            "name": "",
            "dept": "",
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "name":
                xeno_output["name"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "dept":
                xeno_output["dept"] += xeno_entity["value"]

    elif xeno_intent == "mod_man":
        xeno_output = {
            "orderType": "ModMan"
        }

    elif xeno_intent == "get_man_dept":
        xeno_output = {
            "orderType": "GetManDept",
            "name": ""
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "PERSON":
                xeno_output["name"] += xeno_entity["value"]

    elif xeno_intent == "get_man":
        xeno_output = {
            "orderType": "GetMan",
            "name": "",
            "dept": ""
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "name":
                xeno_output["name"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "dept":
                xeno_output["dept"] += xeno_entity["value"]

    elif xeno_intent == "add_dept":
        xeno_output = {
            "orderType": "AddDept",
            "dept": ""
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "dept":
                xeno_output["dept"] += xeno_entity["value"]

    elif xeno_intent == "del_dept":
        xeno_output = {
            "orderType": "DelDept",
            "dept": ""
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "dept":
                xeno_output["dept"] += xeno_entity["value"]

    elif xeno_intent == "get_plan":
        xeno_output = {
            "orderType": "GetPlan"
        }

    elif xeno_intent == "get_plan_by_man":
        xeno_output = {
            "orderType": "GetPlanByMan"
        }

    elif xeno_intent == "add_plan":
        xeno_output = {
            "orderType": "AddPlan"
        }

    elif xeno_intent == "mod_plan":
        xeno_output = {
            "orderType": "ModPlan"
        }

    elif xeno_intent == "get_notes":
        xeno_output = {
            "orderType": "GetNotes"
        }

    elif xeno_intent == "add_note":
        xeno_output = {
            "orderType": "AddNote"
        }

    elif xeno_intent == "mod_note":
        xeno_output = {
            "orderType": "ModNote"
        }

    else:
        xeno_output = {
            "orderType": ""
        }

    print(xeno_output)
