import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import Scene from '../views/scene.vue';
import Audience from '../views/audience.vue';
import App from '../App.vue';

const role = 2;  // 玩家角色，0 为观战者，1 为玩家1，2 为玩家2
const room = Math.floor(Math.random() * 4 + 1);  // 房间号，1～4
const userId = Math.floor(Math.random() * 2 + 3);  // 玩家 id
const name = "Player" + userId;;  // 玩家名
const modelId = Math.floor(Math.random() * 5 + 1);  // 玩家模型 id

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: {
                name: 'Scene',
                params: {
                    role: role,
                    room: room
                },
            }
        },
        {
            path: '/scene/:role/:room',
            name: 'Scene',
            component: Scene,
            params: {
                role: role,
                room: room,
            }
        },
        {
            // 减少冲突，先另开一个文件（
            path: '/audience/:role',
            name: 'Audience',
            component: Audience,
            params: {
                role: role
            }
        },
        {
            path: '/login',
            component: () => import('../views/LoginPage.vue')
        },
        {
            path: '/open',
            component: ()=> import('../views/OpenView.vue')
        },
        {
            path: '/main',
            component: ()=> import('../views/MainPage.vue')
        },
        {
            path: '/person',
            component: ()=> import('../views/PersonPage.vue')
        },
        {
            path: '/register',
            component: ()=> import('../views/RegisterPage.vue')
        }
    ]
});

export default router


// 存储用户信息到 localStorage
localStorage.setItem('userInfo', JSON.stringify({ name, userId, modelId }));
