<template>
    <div id="progress-bar">
        <div id="progress-bar-fill" :style="{ width: progress + '%' }"></div>
    </div>
</template>

<script>
export default {
    props: {
        progress: {
            type: Number,
            default: 0,
        },
    },
};
</script>

<style>
#progress-bar {
    position: absolute; 
    top: 0; 
    left: 0; 
    width: 100%; 
    height: 100%;
    background-color: #b0e0ff;
    z-index: 9999; 
}

#progress-bar-fill {
    top: 45%;
    left: 10%;
    height: 20px;
    background-color: #1e567b;
    position: absolute;
}
</style>
