import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Login from '@/components/views/Login'
import adminIndex from '@/components/views/adminIndex'

Vue.use(Router)

//定义一个路由，每个路由映射一个组件
export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/adminIndex',
      name: 'index',
      component: adminIndex
    }
  ]
})
