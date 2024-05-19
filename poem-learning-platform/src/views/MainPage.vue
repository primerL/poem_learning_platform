<template>
    <q-layout view="hHr lpR fFr" class="bg">
        <q-page-container>
            <div class="q-pa-md">
                <q-card class="my-card">
                    <img src="../assets/img/bg2.jpeg" height="100px">

                    <q-card-section>
                        <div class="text-h6">Our Changing Planet</div>
                        <div class="text-subtitle2">by John Doe</div>
                    </q-card-section>

                    <q-card-section class="q-pt-none">
                        {{ lorem }}
                    </q-card-section>

                    <q-card class="my-card" flat bordered>
                        <q-card-section horizontal>
                            <q-card-section class="q-pt-xs">
                                <div class="text-overline">Overline</div>
                                <div class="text-h5 q-mt-sm q-mb-xs">Title</div>
                                <div class="text-caption text-grey">
                                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                                    incididunt ut labore et dolore magna aliqua.
                                </div>
                            </q-card-section>

                            <q-card-section class="col-5 flex flex-center">
                                <q-img class="rounded-borders" src="https://cdn.quasar.dev/img/parallax2.jpg" />
                            </q-card-section>
                        </q-card-section>

                        <q-separator />

                        <q-card-actions>
                            <q-btn flat round icon="event" />
                            <q-btn flat>
                                7:30PM
                            </q-btn>
                            <q-btn flat color="primary">
                                Reserve
                            </q-btn>
                        </q-card-actions>
                    </q-card>
                </q-card>
            </div>

        </q-page-container>

    </q-layout>

</template>

<script setup>
import { ref } from 'vue'


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
    margin: 2% auto;
    align-items: center;
    /* 垂直居中 */
}
</style>