<template>
  <div class="login-container" :style="bodyStyle">
    <el-form autoComplete="on" :model="loginForm" :rules="loginRules" ref="loginForm" label-position="left" label-width="0px" class="card-box login-form">
      <h3 class="title">奥特科技管理系统</h3>
      <el-form-item prop="username">
        <span class="svg-container">
          <icon-svg icon-class="username"></icon-svg>
        </span>
        <el-input name="username" type="text" v-model="loginForm.username" autoComplete="on" placeholder="账号"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <icon-svg icon-class="password" ></icon-svg>
        </span>
        <el-input name="password" type="password" @keyup.enter.native="handleLogin" v-model="loginForm.password" autoComplete="on" placeholder="密码"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width:100%;" :loading="loading" @click.native.prevent="handleLogin">
          登录
        </el-button>
      </el-form-item>
      <div style="text-align:center;">
        <img :src="app_url" style="height:120px;height:120px;" /><br/>
        <font style="color:#ffffff;">扫一扫下载APP</font>
      </div>
    </el-form>
  </div>
</template>

<script>
    //import { isWscnEmail } from '@/utils/validate';
    import { getHomeInfo } from '@/api/login.js';

    export default {
      name: 'login',
      data() {
        /*
        const validateEmail = (rule, value, callback) => {
          if (!isWscnEmail(value)) {
            callback(new Error('请输入正确的合法邮箱'));
          } else {
            callback();
          }
        };*/
        const validateUsername = (rule, value, callback) => {
          if (value.length < 1) {
            callback(new Error('请输入正确的账号'));
          } else {
            callback();
          }
        };
        const validatePass = (rule, value, callback) => {
          if (value.length < 6) {
            callback(new Error('密码不能小于6位'));
          } else {
            callback();
          }
        };
        return {
          app_url: undefined,
          loginForm: {
            username: '',
            password: ''
          },
          loginRules: {
            username: [
                { required: true, trigger: 'blur', validator: validateUsername }
            ],
            password: [
                { required: true, trigger: 'blur', validator: validatePass }
            ]
          },
          loading: false,
          bodyStyle: ''
        }
      },
      created() {
        var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height
        if (height < 639) {
          height = 639
        }
        this.bodyStyle = 'height:' + height + 'px'

        var self = this
        window.addEventListener('resize', function() {
          var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height
          if (height < 639) {
            height = 639
          }
          self.bodyStyle = 'height:' + height + 'px'
        })
        
        getHomeInfo({}).then((response) => {
          this.app_url = process.env.BASE_API + '/qrimg?data=' + response.data.APP_DOWN_URL;
        })
      },
      methods: {
        handleLogin() {
          this.$refs.loginForm.validate(valid => {
            if (valid) {
              this.loading = true;
              this.$store.dispatch('Login', this.loginForm).then(() => {
                this.loading = false;
                this.$router.push({ path: '/' });
              }).catch(() => {
                this.loading = false;
              });
            } else {
              console.log('error submit!!');
              return false;
            }
          });
        }
      }
    }
</script>

<style rel="stylesheet/scss" lang="scss">
    @import "src/styles/mixin.scss";
    .tips {
      font-size: 14px;
      color: #fff;
      margin-bottom: 5px;
    }

    .login-container {
      @include relative;
      /*height: 100vh;*/
      background-color: #2d3a4b;
      input:-webkit-autofill {
        -webkit-box-shadow: 0 0 0px 1000px #293444 inset !important;
        -webkit-text-fill-color: #fff !important;
      }
      input {
        background: transparent;
        border: 0px;
        -webkit-appearance: none;
        border-radius: 0px;
        padding: 12px 5px 12px 15px;
        color: #eeeeee;
        height: 47px;
      }
      .el-input {
        display: inline-block;
        height: 47px;
        width: 85%;
      }
      .svg-container {
        padding: 6px 5px 6px 15px;
        color: #889aa4;
      }
      .title {
        font-size: 26px;
        font-weight: 400;
        color: #eeeeee;
        margin: 0px auto 40px auto;
        text-align: center;
        font-weight: bold;
      }
      .login-form {
        position: absolute;
        left: 0;
        right: 0;
        width: 400px;
        padding: 0px 35px 15px 0px;
        margin: 100px auto;
      }
      .el-form-item {
        border: 1px solid rgba(255, 255, 255, 0.1);
        background: rgba(0, 0, 0, 0.1);
        border-radius: 5px;
        color: #454545;
      }
      .forget-pwd {
        color: #fff;
      }
    }
</style>
