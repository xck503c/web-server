<template>
  <div>
    <Form method="post">
      <div>
        <h1>用户登录</h1>
      </div>
      <div>
        <TextBox placeholder="用户名" style="width:100%;height:35px;" name="username"/>
      </div>
      <div>
        <TextBox placeholder="密码" style="width:100%;height:35px;" name="password"/>
      </div>
      <div>
        <CheckBox labelPosition="after" labelWidth="70" />  记住我
      </div>
      <div>
        <LinkButton type="submit" text="登陆" style="width:100%;height:35px;" @click="login"/>
      </div>
      <div>
        <div style="display:inline;">
          <a href="javascript:void(0)">还未注册？</a>
        </div>
        <div style="display:inline;margin-left:170px;">
          <a href="javascript:void(0)">忘记密码？</a>
        </div>
      </div>
    </form>
  </div>
</template>

<script>
  export default {
    methods: {
      login: function(){
        // if(!this.username){
        //   this.$message.error('请输入用户名');
        // }
        //这里要加上this，不然会报错
        this.axios.post('https://127.0.0.1:8081')
          .catch(function (error) {
            console.log(error);
          })
      }
    }
  }
</script>

<style>
  html, body {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
  }

  h1 {
    color: rgb(128, 128, 128);
    text-align: center;
  }

  a:link, a:hover, a:visited, a:active {
    color: rgb(128, 128, 128);
    text-decoration: none;
  }

  form {
    width: 400px;
    min-width: 400px;
    position: absolute;
    left: 40%;
    top: 15%;
    margin: 0;
    padding: 30px;
    background-color: white;
    border: 2px solid rgba(128, 128, 128, 0.2);
    border-radius: 10px;
  }

  form div {
    margin-bottom: 10px;
  }
</style>
