<template>
</template>
<script setup>
import { onMounted } from 'vue';
import axios from 'axios';


onMounted(() => {
    let instance = axios.create({
        baseURL: 'http://127.0.0.1:4523/m1/4199717-0-default',
    })

    instance.get('/test').then(res => {
        console.log(res.data);
    })
});


</script>