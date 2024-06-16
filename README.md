## 前端镜像打包

已经重新修改了打包的命令

```
npm run build
```
再编写运行dockerfile即可

## 包管理提醒
因为使用的源不一样，在package-lock.json中可能会有冲突，保证package.json中的包是最新的，然后删除package-lock.json，重新npm install

已经重新做了删减和合并，保证了运行不会有包报错，如果有冲突优先选择相信远程（
包环境乱了可以删除重新npm install

安装了新的包 如果运行有报错可以`npm install`

## 前端运行
`npm run dev`

按 `1` 进入准备状态，按 `2` 退出准备状态，一轮比赛结束后按 `3` 重新开启一场比赛。  
非比赛状态时按`0`可以退出，跳转到main界面。  
按下 `a`, `b`, `c`, `d` 表示题目选项。  
按下 `上下左右` 键进行移动，按下 `空格` 键表示跳跃。    

注册页允许选择5个模型  
目前与后端约定：  
1: 流萤  
2: 罗刹  
3: 李素裳  
4: 驭空  
5: 饮月君  

### 注意⚠️
需限制观众在比赛开始后不得进入房间

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
response:
{
    "userId": 1,
    "username": "newuser",
    "model": 1
}
```

#### 过去一周的比赛场数

```
GET http://localhost:2345/api/users/contest/num/{userId}
request body:(list)
[
    19, 
    13,
    15,
    19,
    15,
    10,
    13
]#分别对应周一到周日
```

#### 过去一周的比赛胜率

```
GET http://localhost:2345/api/users/contest/win/rate/{userId}
request body:(list)
[
    0.3,
    0.3076923076923077,
    0.5333333333333333,
    0.42105263157894735,
    0.3333333333333333,
    0.3,
    0.6153846153846154
]#分别对应周一到周日
```

#### 总胜率(放中间的)

```
GET http://localhost:2345/api/users/win/rate/total/{userId}
response: (double)
0.4391025641025641
```

#### AI解析题目：

```
GET http://localhost:2345/api/review/explain?message=题目是“不识庐山真面目，只缘身在此山中。”“缘”的意思是(  )。”，答案是“因为”。请帮我解释为什么
# 把题目和正确答案拼接一个字符串给后端
response:(string)
这句诗出自宋代文学家苏轼的《题西林壁》，全句是：“横看成岭侧成峰，远近高低各不同。不识庐山真面目，只缘身在此山中。”这句诗通过描写庐山的景色，表达了诗人对于事物认识的深刻哲理。

在这句诗中，“缘”字的意思是“因为”，用来表达原因和结果的关系。整句诗的意思是说，由于诗人身处庐山之中，所以无法看到庐山的全貌。这里的“真面目”指的是庐山的真正面貌或本质。诗人通过这种表达，暗示了人们在认识事物时往往因为局限在事物之中，而难以全面地认识和理解它。

所以，答案是“因为”，它解释了为什么诗人不能识别庐山的真面目，即因为诗人自己就处在这座山中，受到了位置和视角的限制。
```

#### 获取错题

```
GET http://localhost:2345/api/question/review/{userId}
response:
{	"questionId":214,
	"question":"下列诗句中，有错别字的一项是 (       )",
	"answer":3,
	"options":["候蛩凄断，人语西风岸。",
	"月落沙平江似练，望尽芦花无雁。",
	"黯教愁损兰成，可怜夜夜关情。", #answer=3表示答案
	"只有一枝梧叶，不知多少秋声。"],
	"type":"review"
}
```

#### 房间相关

roomID以字符串的形式给：room1, room2, room3, room4

```
进入房间，增加人数
POST http://localhost:2345/rooms/{roomID}/increment
response:200 success

离开房间，减少人数
POST http://localhost:2345/rooms/{roomID}/decrement
response:200 success

进入前的查询人数：
GET http://localhost:2345/rooms/{roomID}/count
response: int
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

### Redis

启动了就行
