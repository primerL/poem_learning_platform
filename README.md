# poem_learning_platform

## 前端运行
`npm run dev`

按 `1` 进入准备状态，按 `2` 退出准备状态，一轮比赛结束后按 `3` 重新开启一场比赛。  
按下 `a`, `b`, `c`, `d` 表示题目选项。  
按下 `上下左右` 键进行移动。  

websocket好像没办法直接模拟，所以等待后端😘（标了一些 TODO 表示可能需要与后端进行交互的地方）



## 后端接口

### 2D相关

#### 注册

**post** http://localhost:8080/api/users/register

**body:**

{

 "username": "newuser",

 "password": "password123",

 "model": 1

}

#### 登录

**post** http://localhost:8080/api/users/login

**body:**

{

 "username": "newuser",

 "password": "password123",

}

### 3D相关

#### 获取题目

**get** http://localhost:8080/api/question/{id}

**response body:**

{

  "questionId": 15,

  "content": "白居易《问刘十九》诗有“绿蚁新醅酒, 红泥小火炉”句, 请问诗中的“绿蚁”指的是什么?(  )",

  "answer": 1,

  "options": [

​    {

​      "optionId": 43,

​      "content": "酒上浮起的绿色泡沫"

​    },

​    {

​      "optionId": 44,

​      "content": "绿色的蚂蚁"

​    },

​    {

​      "optionId": 45,

​      "content": "茶"

​    }

  ]

}
