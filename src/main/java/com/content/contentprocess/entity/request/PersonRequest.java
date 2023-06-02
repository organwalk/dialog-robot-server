package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class PersonRequest {
    private String action;
    private List<Dept> dept;
    @Data
    static public class Dept {
        private List<PersonRequest.Users> users;
    }

    @Data
    static public class Users {
        private String id;
        private String name;
        private String mobile;
        private long sequence;
        private long orgId;
        private String privilege;
    }
}
