<template>
    <div class="wrapper">
        <img src="../assets/img/bg2.jpeg" class="side-image right-image" />
        <div>
            <q-btn class="report" style="background: teal; color: white" label="查看学习报告" @click="toPerson()" />
        </div>
        <div class="background">
            <img src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/756881/textured_paper_%402X.png" alt="" />
        </div>
        <div class="item-bg" ref="itemBg"></div>

        <div class="news-slider">
            <div class="news-slider__wrp swiper-wrapper">
                <div class="news-slider__item swiper-slide" v-for="(newsItem, index) in newsItems" :key="index">
                    <a href="#" class="news__item">
                        <div class="news-date">
                            <span class="news-date__title">{{ newsItem.date.day }}</span>
                            <span class="news-date__txt">{{ newsItem.date.month }}</span>
                        </div>
                        <div class="news__title">{{ newsItem.title }}</div>
                        <!-- <p class="news__txt">{{ newsItem.text }}</p> -->
                        <div class="news__img">
                            <img :src="newsItem.image" alt="news" />
                        </div>

                        <div class="q-pa-md row">
                            <q-btn color="teal-4">
                                <q-icon left size="3xs" name="map" @click="toScene(newsItem)" />
                                <div>参赛</div>
                            </q-btn>
                            <q-btn color="white">
                                <q-icon left size="3xs" name="local_florist" color="teal-4" @click="toAudience(newsItem)"/>
                                <div class="text-black">观赛</div>
                            </q-btn>
                        </div>

                    </a>
                </div>
            </div>

            <div class="news-slider__ctr">
                <div class="news-slider__arrows">
                    <button class="news-slider__arrow news-slider-prev" @click="slidePrev()">
                        <span class="icon-font">
                            <svg class="icon icon-arrow-left">
                                <use xlink:href="#icon-arrow-left"></use>
                            </svg>
                        </span>
                    </button>
                    <button class="news-slider__arrow news-slider-next" @click="slideNext()">
                        <span class="icon-font">
                            <svg class="icon icon-arrow-right">
                                <use xlink:href="#icon-arrow-right"></use>
                            </svg>
                        </span>
                    </button>
                </div>
                <div class="news-slider__pagination"></div>
            </div>
        </div>
        <svg hidden="hidden">
            <defs>
                <symbol id="icon-arrow-left" viewBox="0 0 32 32">
                    <title>arrow-left</title>
                    <path
                        d="M0.704 17.696l9.856 9.856c0.896 0.896 2.432 0.896 3.328 0s0.896-2.432 0-3.328l-5.792-5.856h21.568c1.312 0 2.368-1.056 2.368-2.368s-1.056-2.368-2.368-2.368h-21.568l5.824-5.824c0.896-0.896 0.896-2.432 0-3.328-0.48-0.48-1.088-0.704-1.696-0.704s-1.216 0.224-1.696 0.704l-9.824 9.824c-0.448 0.448-0.704 1.056-0.704 1.696s0.224 1.248 0.704 1.696z">
                    </path>
                </symbol>
                <symbol id="icon-arrow-right" viewBox="0 0 32 32">
                    <title>arrow-right</title>
                    <path
                        d="M31.296 14.336l-9.888-9.888c-0.896-0.896-2.432-0.896-3.328 0s-0.896 2.432 0 3.328l5.824 5.856h-21.536c-1.312 0-2.368 1.056-2.368 2.368s1.056 2.368 2.368 2.368h21.568l-5.856 5.824c-0.896 0.896-0.896 2.432 0 3.328 0.48 0.48 1.088 0.704 1.696 0.704s1.216-0.224 1.696-0.704l9.824-9.824c0.448-0.448 0.704-1.056 0.704-1.696s-0.224-1.248-0.704-1.664z">
                    </path>
                </symbol>

            </defs>
        </svg>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import Swiper from 'swiper';
import 'swiper/swiper-bundle.css';
import $ from 'jquery';
import { useRouter } from 'vue-router'
import axios from 'axios';

const router = useRouter()

const itemBg = ref(null);
let swiper;
// axios.defaults.baseURL = 'http://121.196.228.112:2345'

const newsItems = ref([
    {
        date: { day: '01', month: 'ROOM' },
        title: '绿野天穹',
        text: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s...",
        image: '../../src/assets/img/1.webp'
    },
    {
        date: { day: '02', month: 'ROOM' },
        title: '烈焰流沙',
        text: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s...",
        image: '../../src/assets/img/2.webp',
    },
    {
        date: { day: '03', month: 'ROOM' },
        title: '蓝晶幻境',
        text: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s...",
        image: '../../src/assets/img/3.webp'
    },
    {
        date: { day: '04', month: 'ROOM' },
        title: '危机之地',
        text: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s...",
        image: '../../src/assets/img/4.webp'
    },
    // 继续添加其他newsItems...
]);

const slideNext = () => {
    if (swiper) {
        swiper.slideNext();
    }
};

const slidePrev = () => {
    if (swiper) {
        swiper.slidePrev();
    }
}

function toPerson() {
    console.log('toPerson')
    router.push('/person')
}

function toScene(newsItem) {
    console.log('toScene')
    // console.log(newsItem)
    let roomId = newsItem.date.day
    // roomid从01 转为 1
    if (roomId[0] == '0') {
        roomId = roomId[1]
    }
    // router.push('/scene/' + '1/' + roomId)
    let room = 'room' + roomId
    // room = 'room' + roomId
    console.log('roomId:' ,roomId)
    axios.get(`/api/poem/${room}/count`).then(res => {
        console.log(res)
        let count = res.data
        if (count == 0)
        {
            router.push('/scene/' + '1/' + roomId)
        }
        else if(count == 1)
        {
            router.push('/scene/' + '2/' + roomId)
        }
        else if(count == 2)
            alert('房间已满')

    }).catch(err => {
        console.log(err)
    })
}

function toAudience(newsItem) {
    let roomId = newsItem.date.day
    // roomid从01 转为 1
    if (roomId[0] == '0') {
        roomId = roomId[1]
    }
    console.log('toAudience')
    router.push('/scene/' + '0/' + roomId)
}

onMounted(() => {
    swiper = new Swiper('.news-slider', {
        effect: 'coverflow',
        grabCursor: true,
        loop: true,
        centeredSlides: true,
        keyboard: true,
        spaceBetween: 0,
        slidesPerView: '3',
        speed: 300,
        coverflowEffect: {
            rotate: 0,
            stretch: 0,
            depth: 0,
            modifier: 3,
            slideShadows: false,
        },
        breakpoints: {
            480: {
                spaceBetween: 0,
                centeredSlides: true,
            },
        },
        simulateTouch: true,
        navigation: {
            nextEl: '.news-slider-next',
            prevEl: '.news-slider-prev',
        },
        pagination: {
            el: '.news-slider__pagination',
            clickable: true,
        },
        on: {
            init: function () {
                const activeItem = document.querySelector('.swiper-slide-active .news__item');
                const x = activeItem.getBoundingClientRect().left;
                const y = activeItem.getBoundingClientRect().top;
                const width = activeItem.getBoundingClientRect().width;
                const height = activeItem.getBoundingClientRect().height;

                itemBg.value.style.width = `${width}px`;
                itemBg.value.style.height = `${height}px`;
                itemBg.value.style.transform = `translateX(${x}px) translateY(${y}px)`;
                itemBg.value.classList.add('active');
            },
        },
    });

    swiper.on('touchEnd', function () {
        document.querySelectorAll('.news__item').forEach((item) => item.classList.remove('active'));
        document.querySelector('.swiper-slide-active .news__item').classList.add('active');
        console.log('touchEnd');
    });

    swiper.on('slideChange', function () {
        document.querySelectorAll('.news__item').forEach((item) => item.classList.remove('active'));
        console.log('slideChange');
    });

    swiper.on('slideChangeTransitionEnd', function () {
        document.querySelectorAll('.news__item').forEach((item) => item.classList.remove('active'));
        const activeItem = document.querySelector('.swiper-slide-active .news__item');
        const x = activeItem.getBoundingClientRect().left;
        const y = activeItem.getBoundingClientRect().top;
        const width = activeItem.getBoundingClientRect().width;
        const height = activeItem.getBoundingClientRect().height;

        itemBg.value.style.width = `${width}px`;
        itemBg.value.style.height = `${height}px`;
        itemBg.value.style.transform = `translateX(${x}px) translateY(${y}px)`;
        itemBg.value.classList.add('active');
        activeItem.classList.add('active');
        console.log('slideChangeTransitionEnd');
    });

    if (window.innerWidth > 800) {
        $(document).on('mouseover', '.news__item', function () {
            const x = this.getBoundingClientRect().left;
            const y = this.getBoundingClientRect().top;
            const width = this.getBoundingClientRect().width;
            const height = this.getBoundingClientRect().height;

            itemBg.value.style.width = `${width}px`;
            itemBg.value.style.height = `${height}px`;
            itemBg.value.style.transform = `translateX(${x}px) translateY(${y}px)`;
            itemBg.value.classList.add('active');
        });

        $(document).on('mouseleave', '.news__item', function () {
            itemBg.value.classList.remove('active');
        });
    }
});
</script>

<style scoped>
html {
    position: relative;
    overflow-x: hidden !important;
}

body {
    font-family: "Quicksand", sans-serif;
}

a,
a:hover {
    text-decoration: none;
}

.icon {
    display: inline-block;
    width: 1em;
    height: 1em;
    stroke-width: 0;
    stroke: currentColor;
    fill: currentColor;
}

.background {
    position: fixed;
    width: 100%;
    height: 100%;
    left: 0;
    top: 0;
}

.background:after {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/756881/textured_paper_%402X.png);
    background-blend-mode: multiply;
    background-color: #A9CEC2;
    background-repeat: repeat;
    background-size: 800px;
}

/* .background img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    pointer-events: none;
    user-select: none;
} */
.side-image {
    position: absolute;
    height: 100vh;
    top: 0;
    bottom: 0;
}

.right-image {
    right: 0;
    width: 10%;
    position: absolute;
    z-index: 20;
    opacity: 0.7;
}

.side-image:after {
    position: absolute;
    height: 100vh;
    top: 0;
    bottom: 0;
}


.right-image:after {
    right: 0;
    width: 10%;
    z-index: 20;
    opacity: 0.7;
}

.report {
    position: absolute;
    top: 96%;
    right: 20%;
    transform: translateY(-50%);
    z-index: 20;
}

.report:after {
    position: absolute;
    top: 92%;
    right: 20%;
    transform: translateY(-50%);
    z-index: 20;
}

.item-bg {
    width: 300px;
    height: 500px;
    position: absolute;
    top: 30px;
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 6px 26px 6px rgba(0, 0, 0, 0.25);
    opacity: 0;
    transition: all 0.3s;
    left: -30px;
}

.item-bg.active {
    left: 0;
    top: 0;
    opacity: 1;
}

.news-slider {
    z-index: 2;
    max-width: 1300px;
    margin-left: auto;
    margin-right: auto;
    margin-top: 60px;
}

@media screen and (max-width: 1300px) {
    .news-slider {
        max-width: 1000px;
    }
}

@media screen and (max-width: 576px) {
    .news-slider {
        margin-top: 45px;
    }
}

.news-slider__wrp {
    display: flex;
    align-items: flex-start;
    position: relative;
    z-index: 2;
}

.news-slider__item {
    width: 400px;
    flex-shrink: 0;
}

@media screen and (max-width: 992px) {
    .news-slider__item {
        width: 340px;
    }
}

.news-slider__item.swiper-slide {
    opacity: 0;
    pointer-events: none;
    transition: all 0.3s;
}

.news-slider__item.swiper-slide-active,
.news-slider__item.swiper-slide-prev,
.news-slider__item.swiper-slide-next {
    opacity: 1;
    pointer-events: auto;
}

.news-slider__ctr {
    position: relative;
    z-index: 12;
}

.news-slider__arrow {
    background: #fff;
    border: none;
    display: inline-flex;
    width: 50px;
    height: 50px;
    justify-content: center;
    align-items: center;
    box-shadow: 0 6px 26px 6px rgba(0, 0, 0, 0.25);
    border-radius: 50%;
    position: absolute;
    top: 50%;
    z-index: 12;
    cursor: pointer;
    outline: none !important;
}

.news-slider__arrow:focus {
    outline: none !important;
}

.news-slider__arrow .icon-font {
    display: inline-flex;
}

.news-slider__arrow.news-slider-prev {
    left: 15px;
    transform: translateY(-50%);
}

.news-slider__arrow.news-slider-next {
    right: 15px;
    transform: translateY(-50%);
}

.news-slider__pagination {
    text-align: center;
    margin-top: 50px;
}

.news-slider__pagination .swiper-pagination-bullet {
    width: 13px;
    height: 10px;
    display: inline-block;
    background: #fff;
    opacity: 0.2;
    margin: 0 5px;
    border-radius: 20px;
    transition: opacity 0.5s, background-color 0.5s, width 0.5s;
    transition-delay: 0.5s, 0.5s, 0s;
}

.news-slider__pagination .swiper-pagination-bullet-active {
    opacity: 1;
    background: #ffffff;
    width: 100px;
    transition-delay: 0s;
}

@media screen and (max-width: 576px) {
    .news-slider__pagination .swiper-pagination-bullet-active {
        width: 70px;
    }
}

.news__item {
    padding: 40px;
    color: #fff;
    border-radius: 10px;
    display: block;
    transition: all 0.3s;
}

@media screen and (min-width: 800px) {
    .news__item:hover {
        color: #222222;
        transition-delay: 0.1s;
    }

    .news__item:hover .news-date,
    .news__item:hover .news__title,
    .news__item:hover .news__txt {
        opacity: 1;
        transition-delay: 0.1s;
    }

    .news__item:hover .news__img {
        box-shadow: none;
    }
}

.news__item.active {
    color: #222222;
}

.news__item.active .news-date,
.news__item.active .news__title,
.news__item.active .news__txt {
    opacity: 1;
}

.news__item.active .news__img {
    box-shadow: none;
}

@media screen and (max-width: 992px) {
    .news__item {
        padding: 30px;
    }
}

@media screen and (max-width: 576px) {
    .news__item {
        padding: 20px;
    }
}

.news-date {
    padding-bottom: 20px;
    margin-bottom: 20px;
    border-bottom: 2px solid;
    display: inline-block;
    opacity: 0.7;
    transition: opacity 0.3s;
}

@media screen and (max-width: 576px) {
    .news-date {
        margin-bottom: 10px;
        display: inline-flex;
        align-items: center;
        padding-bottom: 0;
    }
}

.news-date__title {
    display: block;
    font-size: 32px;
    margin-bottom: 10px;
    font-weight: 500;
}

@media screen and (max-width: 576px) {
    .news-date__title {
        margin-right: 10px;
    }
}

.news-date__txt {
    font-size: 16px;
}

.news__title {
    font-size: 25px;
    font-weight: 500;
    opacity: 0.7;
    margin-top: 10px;
    margin-bottom: 15px;
    transition: opacity 0.3s;
}

@media screen and (max-width: 576px) {
    .news__title {
        font-size: 22px;
        margin-bottom: 10px;
    }
}

.news__txt {
    margin: 10px 0;
    line-height: 1.6em;
    font-size: 15px;
    opacity: 0.7;
    transition: opacity 0.3s;
}

.news__img {
    border-radius: 10px;
    box-shadow: 0 6px 26px 6px rgba(0, 0, 0, 0.25);
    height: 200px;
    margin-top: 30px;
    width: 90%;
    transition: all 0.3s;
    transform-origin: 0% 0%;
}

@media screen and (max-width: 576px) {
    .news__img {
        height: 180px;
        margin-top: 20px;
    }
}

.news__img img {
    max-width: 100%;
    border-radius: 10px;
    height: 100%;
    width: 100%;
}
</style>