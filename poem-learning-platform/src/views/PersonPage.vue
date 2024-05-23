<template>
    <div class="q-pa-md bg-container">
        <img src="../assets/img/bg4.png" class="side-image left-image" />
        <img src="../assets/img/bg5.png" class="side-image right-image" />
        <div>
            <q-btn color="orange" label="错题复习" class="button-on-bg" @click="toReview()" />
        </div>
        <q-card class="my-card" id="report">
            <q-card-section class="text-container">
                <div class="text-h4 font1">学习报告</div>
            </q-card-section>
            <q-scroll-area style="height: 90%; width: 90%; margin: auto;">
                <div class="relative-container">
                    <!-- 将按钮放在图片bg6上 -->
                    <img src="../assets/img/bg6.png" class="content-image" />
                    <q-btn class="button-on-image" round color="white" :label="value" text-color="orange-13"
                        size="30px" />
                    <div id="chart" class="chart-container"></div>
                    <div id="bar-chart" class="bar-chart-container"></div>
                </div>

                <div class="q-px-lg q-pb-md">
                    <q-timeline color="secondary">
                        <q-timeline-entry title="学习寄语" subtitle="2023-05-22">
                            <div>
                                过去的一年中，你的学习进步了很多，尤其是在诗词鉴赏方面展现出了非凡的才华。然而，学习是一条漫长的路，希望你能保持谦虚，继续努力，挑战自我，不断提高自己的能力和知识水平。记住，成功的道路上没有捷径，只有坚持和努力才能走得更远。愿你在新的一年里再接再厉，取得更辉煌的成绩！
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="对战最多的玩家" subtitle="2023-05-22">
                            <div>
                                你与<strong>玩家A</strong>进行了最多的对战，共进行了<strong>50</strong>次激烈的比赛。在这些比赛中，你们互相切磋，共同进步，形成了良好的竞争关系。希望你们能够继续保持这种积极的竞争，不断提升自己的实力。
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="参与的诗词大赛次数" subtitle="2023-05-22">
                            <div>
                                你一共参与了<strong>30</strong>次诗词大赛，每次比赛你都展现出了坚韧不拔的精神和出色的诗词造诣。其中，你获得了<strong>20</strong>次胜利，胜率达到了<strong>66%</strong>。这些成绩离不开你的努力和付出。希望你能继续保持这种良好的状态，在未来的比赛中取得更好的成绩。
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="最佳表现" subtitle="2023-05-22" color="orange" icon="star">
                            <div>
                                在一次特别的诗词大赛中，你的表现尤为出色，凭借深厚的诗词积累和出色的临场发挥，获得了满分评价，成为了当月的最佳选手。这不仅是对你过去努力的肯定，更是对你未来继续前行的激励。希望你再接再厉，争取在未来的比赛中取得更好的成绩。
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="学习时长" subtitle="2023-05-22">
                            <div>
                                你总共学习了<strong>500</strong>个小时，平均每天学习<strong>1.4</strong>个小时。这些时间不仅让你积累了丰富的知识，也培养了你良好的学习习惯。希望你能够继续坚持这种学习态度，不断挑战自己，提高自己的综合能力。
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="最具挑战的时刻" subtitle="2023-05-22">
                            <div>
                                在一次重要的诗词比赛中，你遇到了前所未有的挑战。尽管比赛过程中充满了困难和挫折，但你没有放弃，凭借坚韧的毅力和不断积累的知识，最终顺利完成了比赛。这次经历不仅提升了你的诗词水平，也让你更加坚定了迎接未来挑战的信心。
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="错题复习" subtitle="2023-05-22">
                            <div>
                                在过去的一年中，你记录并复习了<strong>100</strong>道错题。这些错题涵盖了各个知识点，帮助你发现了自己的薄弱环节。通过对这些错题的分析和总结，你不仅巩固了相关知识，还提高了自己的解题能力。希望你能够继续保持这种良好的学习习惯，通过不断复习错题，进一步提升自己的知识水平和考试成绩。
                            </div>
                        </q-timeline-entry>
                    </q-timeline>
                </div>
            </q-scroll-area>
        </q-card>

        <div class="q-pa-md q-gutter-sm">
            <q-dialog v-model="layout">
                <q-layout view="Lhh lpR fff" container class="bg-white">
                    <q-header class="bg-teal">
                        <q-toolbar>
                            <q-toolbar-title>错题复习</q-toolbar-title>
                            <q-btn flat v-close-popup round dense icon="close" />
                        </q-toolbar>
                    </q-header>

                    <q-footer class="bg-black text-white">
                        <q-toolbar inset>
                            <q-toolbar-title>Footer</q-toolbar-title>
                        </q-toolbar>
                    </q-footer>

                    <q-page-container>
                        <q-page padding>
                            <q-chip>
                                <q-avatar icon="bookmark" color="red" text-color="white" />
                                题目
                            </q-chip>
                            <div class="q-mt-md text-center">
                                {{ question }}
                            </div>

                            <div class="q-mt-md" v-for="option in options" :key="option">
                                <q-chip class="glossy" color="orange" text-color="white" icon-right="star">
                                    选项 {{ options.indexOf(option) + 1 }}
                                </q-chip>
                                {{ option }}
                            </div>

                            <q-splitter />

                            <div class="q-mt-md">
                                <q-chip>
                                    <q-avatar icon="bookmark" color="red" text-color="white" />
                                    正确答案为 {{ answer }}
                                </q-chip>
                            </div>

                            <div class="q-mt-md">
                                <q-chip>
                                    <q-avatar>
                                        <img src="https://cdn.quasar.dev/img/avatar5.jpg">
                                    </q-avatar>
                                    螺丝咕姆解析
                                </q-chip>

                            </div>

                        </q-page>
                    </q-page-container>
                </q-layout>
            </q-dialog>
        </div>

        <div class="q-pa-md q-gutter-sm">
            <q-dialog v-model="carousel">
                <q-carousel transition-prev="slide-right" transition-next="slide-left" swipeable animated
                    v-model="slide" control-color="primary" navigation-icon="radio_button_unchecked" navigation padding
                    height="200px" class="bg-white shadow-1 rounded-borders">
                    <q-carousel-slide :name="1" class="column no-wrap flex-center">
                        <q-icon name="style" color="primary" size="56px" />
                        <div class="q-mt-md text-center">
                            {{ lorem }}
                        </div>
                    </q-carousel-slide>
                </q-carousel>
            </q-dialog>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts';
import axios from 'axios';

const value = ref('81%')
const layout = ref(false)
axios.defaults.baseURL = "http://localhost:2345";
const userId = localStorage.getItem('userId')
const carousel = ref(false)
const slide = ref(1)
const lorem = ref('恭喜你没有错题需要复习，再接再厉吧')
const question = ref(null)
const options = ref([])
const answer = ref(null)
const explanation = ref(null)
axios.defaults.baseURL = 'http://localhost:2345'

function toReview() {
    // layout.value = true

    axios.get(`/api/question/review/${userId}`).then(res => {
        console.log(res.data)
        let data = res.data
        if (data['questionId'] == null) {
            carousel.value = true
        }
        else {
            layout.value = true
            question.value = data['question']
            options.value = data['options']
            answer.value = data['answer']
            let ans = options.value[answer.value-1]
            let message = "题目是"+ question.value.toString() + "正确选项是" + ans.toString()
            let m = "你好"
            console.log(message)
            axios.get(`/api/review/explain?message=${message}`).then(res => {
                explanation.value = res.data
                console.log(explanation.value)
            }).catch(err => {
                console.log(err)
            })
        }
    }).catch(err => {
        console.log(err)
    })

}

onMounted(() => {


    // 折线图
    const chartDom = document.getElementById('chart')
    const myChart = echarts.init(chartDom)
    const option = {
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [820, 932, 901, 934, 1290, 1330, 1320],
                type: 'line'
            }
        ]
    }
    myChart.setOption(option)

    // 柱状图
    const barChartDom = document.getElementById('bar-chart')
    const barChart = echarts.init(barChartDom)
    const barOption = {
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [120, 200, 150, 80, 70, 110, 130],
                type: 'bar'
            }
        ]
    }
    barChart.setOption(barOption)
})
</script>

<style scoped>
.bg-container {
    background-color: rgba(237, 253, 250, 0.9);
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100vw;
    position: relative;
}

.side-image {
    position: absolute;
    height: 100vh;
    top: 0;
    bottom: 0;
}

.left-image {
    left: 0;
    width: 22%;
}

.right-image {
    right: 0;
    width: 23%;
}

.content-image {
    width: 100%;
    height: 100%;
    object-fit: contain;
}

.middle-progress {
    position: absolute;
    top: 0%;
    left: 30%;
    transform: translate(-50%, -50%);
    z-index: 30;
}

.my-card {
    position: relative;
    z-index: 1;
    width: calc(100% - 40%);
    /* Adjust this value based on the desired width of the card */
    height: 105%;
    /* Adjust this value based on the desired height of the card */
    max-width: 1000px;
    /* margin-left: 1%; */
    background: rgba(237, 253, 250);
    /* Adding some transparency to match the design */
}

.text-container {
    flex-grow: 1;
    /* 填充剩余空间 */
}

.relative-container {
    position: relative;
}

.chart-container {
    position: absolute;
    top: -14%;
    /* Adjust as needed */
    left: 10px;
    /* Adjust as needed */
    width: 40%;
    /* Adjust as needed */
    height: 240px;
    /* Adjust as needed */
}

.bar-chart-container {
    position: absolute;
    top: -14%;
    /* 调整位置 */
    right: 5px;
    /* 调整位置 */
    width: 40%;
    /* 调整尺寸 */
    height: 240px;
    /* 调整尺寸 */
}

/* 按钮在图片上的位置 */
.button-on-image {
    position: absolute;
    top: 25%;
    left: 49.5%;
    transform: translate(-50%, -50%);
    z-index: 10;
}

.button-on-bg {
    position: absolute;
    top: 90%;
    left: 85%;
    transform: translate(-50%, -50%);
    z-index: 20;
}

/* 引入自定义字体 */
@font-face {
    font-family: "MyCustomFont";
    /* 自定义字体名称 */
    src: url("../assets/ttf/1.ttf") format("truetype");
    /* 字体文件路径 */
    /* 其他字体属性，如字重、斜体等 */
}

/* 应用自定义字体到元素 */
.text-container {
    font-family: "MyCustomFont", Arial, sans-serif;
    /* 使用自定义字体 */
}
</style>
