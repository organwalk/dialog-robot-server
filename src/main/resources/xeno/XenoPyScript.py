from XenoNLPRequest import xeno_nlper

import os
import sys
import json
import contextlib
import io

with contextlib.redirect_stdout(io.StringIO()):
    import jionlp as xeno_jio


xeno_input = sys.argv[1]  # 从脚本获取原文

xeno_response, xeno_status = xeno_nlper(xeno_input)  # 获取语言理解结果及状态码
# print("拿到了：" + json.dumps(xeno_response) + "\n")
if xeno_status == 200:
    # 解析意图
    xeno_intent = xeno_response["intent"]["name"]
    # 解析原文
    xeno_text = xeno_response["text"]
    # 解析实体
    xeno_entities = xeno_response["entities"]
    # 输出规范
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

        try:
            url_detected = xeno_jio.extract_url(xeno_text)
        except ValueError:
            url_detected = None
        if url_detected is not None:
            xeno_output["url"] = url_detected[0]

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "title":
                xeno_output["title"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]

    elif xeno_intent == "voc_msg":
        xeno_output = {
            "orderType": "VocMsg",
            "url": "",
            "object": []
        }

        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])

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
                if xeno_output["title"] != "" or xeno_entity["confidence_entity"] > 0.40:
                    xeno_output["title"] = xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                # 只取一个，要么是还没有值的时候，要么就是置信度较高，后来者居上
                if xeno_output["object"] != "" or xeno_entity["confidence_entity"] > 0.40:
                    xeno_output["content"] = xeno_entity["value"]
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
        PERSON = []
        for xeno_entity in xeno_entities:
            if xeno_entity["entity"] == "object":
                xeno_output["object"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "desc":
                xeno_output["desc"].append(xeno_entity["value"])
            elif xeno_entity["entity"] == "title":
                xeno_output["title"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "content":
                xeno_output["content"] += xeno_entity["value"]
            elif xeno_entity["entity"] == "PERSON":
                PERSON.append(xeno_entity["value"])
        # 兜个底
        if not xeno_output["object"]:
            xeno_output["object"] = PERSON

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

    elif xeno_intent == "send_msg":
        xeno_output = {
            "orderType": "SendMsg"
        }

    elif xeno_intent == "time_query_plan":
        time_detected = []
        try:
            time_span = xeno_jio.parse_time(xeno_text)
        except ValueError:
            time_span = None

        if time_span is not None:
            time_detected = time_span['time']
            xeno_output = {
                "orderType": "TimeQueryPlan",
                "timeDetected": time_detected
            }
        else:
            xeno_output = {
                "orderType": "TimeQueryPlan"
            }

    elif xeno_intent == "name_query_plan":
        name_plan_maker = ''
        for xeno_entity in xeno_entities:
            if xeno_entity['entity'] == 'PERSON':
                name_plan_maker = xeno_entity['value']

        if name_plan_maker:
            xeno_output = {
                "orderType": "NameQueryPlan",
                "planName": name_plan_maker
            }
        else:
            xeno_output = {
                "orderType": "NameQueryPlan",
                "nameQueryPlanNone": True
            }

    elif xeno_intent == "content_query_plan":
        plan_content = ''
        for xeno_entity in xeno_entities:
            if xeno_entity['entity'] == 'plan-content':
                plan_content = xeno_entity['value']
        if plan_content:
            xeno_output = {
                "orderType": "ContentQueryPlan",
                "planContent": plan_content
            }
        else:
            xeno_output = {
                "orderType": "ContentQueryPlan",
                "contentQueryPlanNone": True
            }

    elif xeno_intent == "fast_add_notes":
        xeno_output = {
            "orderType": "FastAddNotes",
            "timeDetected": [],
            "noteObject": [],
            "noteContent": ""
        }

        for xeno_entity in xeno_entities:
            if xeno_entity['entity'] == 'note-object':
                xeno_output['noteObject'].append(xeno_entity['value'])
            elif xeno_entity['entity'] == 'note-content':
                xeno_output['noteContent'] = xeno_entity['value']

        try:
            time_span = xeno_jio.parse_time(xeno_text)
        except ValueError:
            time_span = None
        if time_span is not None:
            xeno_output['timeDetected'] = time_span['time']

        if xeno_output['timeDetected'] == [] and xeno_output['noteObject'] == [] and xeno_output['noteContent'] == '':
            xeno_output = {
                "orderType": "FastAddNotes",
                "fastAddNotesNone": True
            }

    elif xeno_intent == "query_done":
        xeno_output = {
            "orderType": "FastQueryNotes",
            "notestatus": "done"
        }

    elif xeno_intent == "query_undone":
        xeno_output = {
            "orderType": "FastQueryNotes",
            "notestatus": "undone"
        }

    else:
        xeno_output = {
            "orderType": ""
        }

    print(xeno_output)
