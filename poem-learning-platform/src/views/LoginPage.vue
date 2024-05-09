<template>
    <q-layout view="hHr lpR fFr" class="bg">
        <q-page-container>
            <div class="q-pa-md">
                <q-card class="my-card height:200px" flat bordered>
                    <q-card-section horizontal>
                        <q-card-section>
                            <q-card-section>
                                <div class="flex">
                                    <div class="image-container">
                                        <q-img height="100px" width="100px" src="../assets/img/logo.png" />
                                    </div>
                                    <div class="text-container">
                                        <div class="text-h4 font1">诗意联结，共襄盛会</div>
                                    </div>
                                </div>

                            </q-card-section>

                            <q-tabs v-model="tab" class="text-teal">
                                <q-tab label="Login" name="login" />
                                
                            </q-tabs>

                            <q-separator />

                            <q-tab-panels v-model="tab" animated>
                                <q-tab-panel name="login">
                                    <div class="q-pa-md" style="max-width: 400px">

                                        <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
                                            <q-input filled v-model="login_name" label="Your name *"
                                                hint="please type your name" lazy-rules
                                                :rules="[val => val && val.length > 0 || 'Please type something']" />

                                            <q-input filled v-model="login_password" label="Your password *" lazy-rules
                                                :rules="[
                                val => val !== null && val !== '' || 'Please type your password',
                            ]" />

                                            <q-toggle v-model="accept" label="I accept the license and terms" />

                                            <div>
                                                <q-btn label="Submit" type="submit" color="teal" />
                                                <q-btn label="Reset" type="reset" color="teal" flat class="q-ml-sm" />
                                            </div>
                                        </q-form>

                                    </div>
                                </q-tab-panel>
                            </q-tab-panels>

                        </q-card-section>



                        <q-img class="col-5" src="../assets/img/bg.png" />
                    </q-card-section>
                </q-card>
            </div>

        </q-page-container>

    </q-layout>
</template>


<script setup>
import { ref } from 'vue'

const tab = ref('login')
const splitterModel = ref(20)
const lorem = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.'
</script>

<style>
.bg {
    background-image: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/756881/textured_paper_%402X.png);
    background-blend-mode: multiply;
    background-color: #A9CEC2;
    background-repeat: repeat;
    background-size: 800px;
}

.my-card {
    width: 70%;
    height: 70%;
    margin: 5% auto;
    align-items: center;
    /* 垂直居中 */
}

.flex {
    display: flex;
    align-items: center;
    /* 垂直居中 */
}

.image-container {
    margin-right: 10px;
    /* 图片与文字之间的间距 */
}

.text-container {
    flex-grow: 1;
    /* 填充剩余空间 */
}

/* 引入自定义字体 */
@font-face {
    font-family: 'MyCustomFont';
    /* 自定义字体名称 */
    src: url('../assets/ttf/1.ttf') format('truetype');
    /* 字体文件路径 */
    /* 其他字体属性，如字重、斜体等 */
}

/* 应用自定义字体到元素 */
.text-container {
    font-family: 'MyCustomFont', Arial, sans-serif;
    /* 使用自定义字体 */
}
</style>