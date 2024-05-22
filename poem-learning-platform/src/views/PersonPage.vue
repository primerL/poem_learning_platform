<template>
    <div class="q-pa-md bg-container">
        <img src="../assets/img/bg4.png" class="side-image left-image" />
        <img src="../assets/img/bg5.png" class="side-image right-image" />
        <q-card class="my-card" id="report">
            <q-card-section class="text-container">
                <div class="text-h4 font1">学习报告</div>
            </q-card-section>
            <!-- <q-separator /> -->

            <q-scroll-area style="height: 90%; width: 90%; margin: auto;">
                <div class="relative-container">
                    <img src="../assets/img/bg6.png" class="content-image" />
                    <div id="chart" class="chart-container"></div>
                    <div id="bar-chart" class="bar-chart-container"></div>
                </div>

                <div class="q-px-lg q-pb-md">
                    <q-timeline color="secondary">

                        <q-timeline-entry title="Event Title" subtitle="February 22, 1986"
                            avatar="https://cdn.quasar.dev/img/avatar2.jpg">
                            <div>
                                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor
                                incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                                exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure
                                dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                                Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
                                mollit anim id est laborum.
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="Event Title" subtitle="February 22, 1986">
                            <div>
                                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor
                                incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                                exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure
                                dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                                Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
                                mollit anim id est laborum.
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="Event Title" subtitle="February 22, 1986" color="orange"
                            icon="done_all">
                            <div>
                                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor
                                incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                                exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure
                                dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                                Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
                                mollit anim id est laborum.
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="Event Title" subtitle="February 22, 1986">
                            <div>
                                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor
                                incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                                exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure
                                dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                                Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
                                mollit anim id est laborum.
                            </div>
                        </q-timeline-entry>

                        <q-timeline-entry title="Event Title" subtitle="February 22, 1986">
                            <div>
                                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor
                                incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                                exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure
                                dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                                Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
                                mollit anim id est laborum.
                            </div>
                        </q-timeline-entry>
                    </q-timeline>
                </div>
            </q-scroll-area>
        </q-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts';
import axios from 'axios'



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

<style>
.bg-container {
    /* background-image: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/756881/textured_paper_%402X.png); */
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
    width: 22%;
}

.content-image {
    width: 100%;
    height: 100%;
    object-fit: contain;
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