<template>
</template>
<script setup>
import { onMounted } from 'vue';
import io from 'socket.io-client';
import axios from 'axios';

onMounted(() => {

    // axios请求
    let instance = axios.create({
        baseURL: 'http://127.0.0.1:4523/m1/4199717-0-default',
    })

    instance.get('/test').then(res => {
        console.log(res.data);
    })

    // io
    const socket = io('ws://127.0.0.1:2002');
    socket.on('connect', () => {
        console.log('connect');
    });

});


</script>