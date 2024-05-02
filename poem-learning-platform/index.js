import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import Scene from './scene.vue';
import App from './App.vue';

const role = 1;  // 玩家角色，0 为观战者，1 为玩家1，2 为玩家2
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { 
            path: '/', 
            redirect: {
                name: 'Scene',
                params: { role: role},
            }
        },
        { 
            path: '/scene/:role', 
            name: 'Scene', 
            component: Scene,
            params: { role: role } 
        }
    ]
});

const app = createApp(App); 
app.use(router); // 使用路由
app.mount('#app'); // 将应用挂载到页面上的 #app 元素上
