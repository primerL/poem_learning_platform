<template>
    <div id="app">
        <router-view></router-view>
        <ProgressBar v-if="isProgressBar" :progress="loadingProgress" />
    </div>
</template>

<script>
import { mapState } from "vuex";
import ProgressBar from "./views/ProgressBar.vue";
export default {
    components: {
        ProgressBar,
    },
    computed: {
        ...mapState(["isLoading"]),
    },
    data() {
        return {
            loadingProgress: 0, // 初始加载进度为0
            isProgressBar: false,
        };
    },
    methods: {
        updateLoadingProgress(progress) {
            this.loadingProgress = progress;
        },
        // 模拟加载过程
        simulateLoading() {
            let progress = 0;
            const interval = setInterval(() => {
                progress += Math.random() * 10;
                if (this.loadingProgress >=80 || progress >= 80) {
                    clearInterval(interval);
                    this.updateLoadingProgress(80);
                } else {
                    this.updateLoadingProgress(progress);
                }
            }, 20); // 每20毫秒更新一次进度
        },
    },
    watch: {
        // 监听 isLoading 的变化
        isLoading(newVal) {
            if (!newVal) {
                // 如果 isLoading 变为 false，立即将进度条变为满（80）
                this.loadingProgress = 80;
                setTimeout(() => {
                    // 500毫秒后隐藏进度条
                    this.isProgressBar = false;
                }, 500);
            } else {
                // 如果 isLoading 变为 true，开始模拟加载过程
                this.isProgressBar = true;
                this.simulateLoading();
            }
        }
    }
};
</script>

<style>
#app {
    height: 100%;
    display: flex;
    width: 100%;
}
</style>
