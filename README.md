## 包管理提醒
已经重新做了删减和合并，保证了运行不会有包报错，如果有冲突优先选择相信远程（
包环境乱了可以删除重新npm install

安装了新的包 如果运行有报错可以`npm install`

## 前端运行
`npm run dev`

按 `1` 进入准备状态，按 `2` 退出准备状态，一轮比赛结束后按 `3` 重新开启一场比赛。  
按下 `a`, `b`, `c`, `d` 表示题目选项。  
按下 `上下左右` 键进行移动。    

注册页允许选择5个模型  
目前与后端约定：  
1: 流萤  
2: 罗刹  
3: 李素裳  
4: 驭空  
5: 饮月君  

**跳转部分localStorage写在了index.js中，需要进行相应的更改**。
跳转到3d场景时可以用localStorage.getItem('selectModelId') 获取到模型选择的参数，所有模型和动作已经更新整理在assets下


## 后端接口

### 2D相关

#### 注册

```
POST http://localhost:2345/api/users/register
request body:
{
 "username": "newuser",
 "password": "password123",
 "model": 1
}
```

#### 登录

```
POST http://localhost:2345/api/users/login
request body:
{
 "username": "newuser",
 "password": "password123",
}
```

### 3D 相关
#### 智能NPC

```
GET http://localhost:2345/api/chat?message=请告诉我一句关于月亮的诗句
response:(string)
当然，关于月亮的诗句有很多，这里给你一句来自中国古代诗人苏轼的名句：
“但愿人长久，千里共婵娟。”
这句诗出自苏轼的《水调歌头·丙辰中秋》，表达了诗人对远方亲人的思念之情，以及对人间美好情感的祝愿。婵娟在这里指的是月亮，诗句通过月亮的意象，寄托了对亲人的思念和对团圆的渴望。
```

## 数据库

### MySQL

生成数据表，运行根目录`poetry.sql`

 ```
 users: 用户信息
 questions: 题库
 options: 题目对应的选项
 contest_results: 比赛结果
 answers: 回答情况
 ```

