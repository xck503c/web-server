<template>
  <div>
    <input type="text" placeholder="用户名" v-model="username">
    <input type="text" placeholder="密码" v-model="password">
    <button v-on:click="login">登录</button>
  </div>
</template>
