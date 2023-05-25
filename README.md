# 自然语言处理解析服务需求实现与设计 2023.05.25

本文档提供对自然语言处理解析服务需求实现的项目设计，并不包含语言处理模型的具体定义。

**技术选型**

- JDK 17

- SpringBoot 3.1.0

## 1.实体类

应该定义一个实体类包entity，内含三个包：request、respond、table。分别用于定义请求体、响应体以及数据库表映射。同时应该引入lombok依赖中的@Data注解和@Bulider注解以减少样板代码的编写，提高程序的可读性与可维护性。

### 1.1 request包

应该对[《自然语言处理解析服务接口定义》](https://organwalk.ink/article/13)中的请求体进行分析。有如下示例定义：

**GroupRequest类**

含有如下字段：

- ‌String action
- ‌List<Group>group 

因此应该继续定义其中的`Group`类：

- ‌long deptId
- ‌long groupId 
- ‌String groupName

**DeptRequest类**

含有如下字段：

- ‌String action
- ‌List <Dept> dept

因此应该继续定义其中的`Dept`类：

- List <Departments>departments

应该继续定义其中的`Department`类：

- ‌long deptId
- ‌long parentId
- ‌String name
- ‌long order

### 1.2 respond 包

应该对[《自然语言处理解析服务接口定义》](https://organwalk.ink/article/13)中的响应体进行分析。有如下示例定义：

**StatusRespond类**

该类只有两个字段，通常用于反馈操作执行状态：

- ‌long status
- ‌boolean success

应该有三个构造方法:

- ‌StatusRespond ok()

  其中设status为200，success为true

- ‌StatusRespond dataNull()

  其中设status为404，success为false

- ‌StatusRespond fail()

  其中设status为500，success为false

**UserRespond类**

该类用于响应当前用户信息：

- ‌long status
- ‌boolen success
- ‌String uid
- ‌String name
- ‌String dept

应该有三个构造方法：

- ‌UserRespond ok(String uid,String name,String dept)

  其中设status为200，success为true

- ‌UserRespond dataNull()

  其中设status为404，success为false

- ‌UserRespond fail()

  其中设status为500，success为false

### 1.3 table包

应该对[《自然语言处理解析服务数据库定义》](https://organwalk.ink/article/14)中的数据库表进行分析。有如下示例定义：

**ScheduleTable类**

该类映射schedule表中字段：

- Integer id
- String uid
- String name
- String content
- String begintime
- String endtime
- String iswarn
- String straddr
- String strdescrip
- String scheduleId
- String action
- List<SMembers> members

**SMembers类**

该类映射s_members表中字段：

- Integer id
- Integer schedule_id
- String uid
- String name

## **2.Mapper类**

应该定义一个mapper包，用于定义与MySQL数据库与Redis数据库的操作

### 2.1 mySQL包

用于对数据库表的操作，应该有两个接口：

**ScheduleMapper接口**

该接口使用SQL注解，应该实现如下方法：

// 部分方法可以直接使用MyBatisPlus的SQL构建器直接在Service调用即可

- 插入日程数据	
- 根据日程ID修改日程数据
- 根据日程ID修改日程的action字段值为cancel
- 根据日程ID删除日程
- 根据用户ID获取日程列表

**NotificationMapper接口**

该接口应该实现如下方法：

// 部分方法可以直接使用MyBatisPlus的SQL构建器直接在Service调用即可

- 插入事项数据	
- 根据事项ID修改事项数据
- 根据事项ID修改事项的action字段值为cancel
- 根据事项ID删除事项
- 根据用户ID获取事项列表

### 2.2 redis包

根据[《自然语言处理解析服务数据库定义》](https://organwalk.ink/article/14)的第五章进行设计，用于对Redis数据库进行操作，应该有两个接口及其实现类，实现类应该放入Impl子包中。

**SaveDataListRedis接口**

该接口定义如下方法：

- 存储企业群信息
- 存储部门列表
- 存储当下部门人员列表
- 存储当前登录用户信息

**SaveDataListRedisImpl类**

该类应该实现接口方法

**GetDataListRedis接口**

该接口定义如下方法：

- 根据群名称获取部门ID与群ID信息
- 根据部门名称获取部门ID、父部门和部门排序信息
- 根据人名获取用户 ID、用户手机号、用户排序与企业 ID信息
- 根据当前登录用户手机号获取该用户的用户 ID、用户姓名与用户所处部门信息
- 根据当前登录用户手机号销毁对应手机号的资源

## 3.Service类

应该定义一个service包，用以实现汇聚数据，调用数据模型，为控制器提供响应数据。实现类应该放入Impl子包中。

### **3.1 ScheduleService接口**

该接口用于定义对客户端提供的日程数据进行处理，并在处理后为控制器提供处理结果，包装响应数据，应该以Respond包中定义的响应实体类作为返回类型，定义如下方法：

- 插入日程数据
- 根据日程ID修改日程数据
- 根据日程ID修改日程的action字段值为cancel
- 根据日程ID删除日程
- 根据用户ID获取日程列表

### **3.2 ScheduleServiceImpl类**

该类应该实现接口方法，返回处理不同状态的处理结果

### **3.3 NotificationService接口**

该接口用于定义对客户端提供的事项数据进行处理，并在处理后为控制器提供处理结果，包装响应数据，应该以Respond包中定义的响应实体类作为返回类型，定义如下方法：

- 插入事项数据
- 根据事项ID修改事项数据
- 根据事项ID修改事项的action字段值为cancel
- 根据事项ID删除事项
- 根据用户ID获取事项列表

### **3.4 NotificationServiceImpl类**

该类应该实现接口方法，返回处理不同状态的处理结果。

### 3.5 ContentService接口

该接口用于定义对客户端提供的对话指令进行处理，并在处理后为控制器提供处理结果，包装响应数据，应该以Respond包中定义的响应实体类作为返回类型，定义如下方法：

- 根据对话指令返回参数模板

### 3.6 ContentServiceImpl类

该类应该实现接口方法，返回处理不同状态的处理结果。其中应该调用语言处理模型，传递对话指令，在获得输出的参数模板后，应该重新包装输出，针对人名、群名使用定义好的redis接口进行ID化。

### 3.7 SystemService接口

该接口用于定义对客户端提供的销毁请求进行处理，并在处理后为控制器提供处理结果，包装响应数据，应该以Respond包中定义的响应实体类作为返回类型，定义如下方法：

- 根据用户ID销毁资源

### 3.8 SystemServiceImpl类

该类应该实现接口方法，返回处理不同状态的处理结果。其中应该根据用户ID调用Redis接口获取对应用户手机号，再根据手机号销毁资源。

## 4. Controller类

应该有一个controller包用以定义controller类。该类作为控制器，应该根据[《自然语言处理解析服务接口定义》](https://organwalk.ink/article/13)进行设计，同时应该使用注解解决跨域问题。

### 4.1 DataController类

该控制器应该实现[《自然语言处理解析服务接口定义》](https://organwalk.ink/article/13)中的第一章内容。

### 4.2 OrderController类

该控制器应该实现[《自然语言处理解析服务接口定义》](https://organwalk.ink/article/13)中的第二章内容，根据请求中不同的action，调用相应的Service

### 4.3 SystemController类

该控制器应该实现[《自然语言处理解析服务接口定义》](https://organwalk.ink/article/13)中的第三章内容，根据请求中不同的action，调用相应的Service



