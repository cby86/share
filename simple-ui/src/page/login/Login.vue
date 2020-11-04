<template>
  <div class="v-bg">
    <el-dialog :title="title"  :visible="true" width="30%" center :show-close="false" :modal="false">
      <el-form label-width="0px" label-position="left" v-loading="fullscreenLoading" :model="loginUser" status-icon :rules="rules" ref="ruleForm"  class="demo-ruleForm">
        <el-form-item  prop="username">
          <el-input type="text" placeholder="用户名" v-model="loginUser.username" auto-complete="off">
            <v-icon slot="prefix" name="icon-people" ></v-icon>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="密码" v-model="loginUser.password" auto-complete="off">
            <v-icon slot="prefix"  name="icon-unlock" ></v-icon>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button style="width:100%" type="primary" @click="submitForm('ruleForm')">登录</el-button>
          <!--          <el-button type="text" @click="resetForm('ruleForm')">重置</el-button>-->
          <!--          <el-button type="text" style="float:right" @click="register('ruleForm')">注册</el-button>-->
        </el-form-item>
      </el-form>
    </el-dialog>
    <div id="particles-js"></div>
  </div>
</template>
<script>
  export default {
    name: 'Login',
    data() {
      return {
        title: process.env.SYSTEM_NAME,
        fullscreenLoading: false,
        loginUser: {
          username: '',
          password: '',
        },
        rules: {
          username: [
            {required: true, message: '用户名不能为空', trigger: 'blur'},
            {min: 2, max: 30, message: '长度在2到30个字符之间', trigger: 'blur'}
          ],
          password: [
            {required: true, message: '密码不能为空', trigger: 'blur'},
            {min: 2, max: 30, message: '长度在2到30个字符之间', trigger: 'blur'}
          ]
        }
      }
    },
    methods: {
      submitForm: function (formName) {
        this.$refs[formName].validate((valid) => {
          const data = {
            username: this.loginUser.username,
            password: this.loginUser.password,
            client_secret: '12345',
            client_id: 'client',
            scope: 'app',
            grant_type: 'password'
          }
          if (valid) {
            // this.fullscreenLoading = true;
            this.$request.post({
              url: '/dologin',
              data: data,
              success: result => {
                // 解析token
                let newVar = {
                  username:result.data.username,
                  times:result.data.times,
                  realName:result.data.realName,
                  totalCount:result.data.totalCount,
                  inner:result.data.inner,
                  role:result.data.authorities && result.data.authorities.length>0 ?result.data.authorities[0].authority:null
                };
                sessionStorage.setItem("token", result.data.token)
                sessionStorage.setItem("userInfo",JSON.stringify(newVar))

                this.$store.dispatch('setUserInfo',newVar)
                this.$router.push('/')
              },
              error: e => {
                this.fullscreenLoading = false;
                this.$message.error(e)
              }
            })
          }
        })
      },
      isEmpty(value) {
        return (
          value === undefined || value === null || (typeof value === 'object' && Object.keys(value).length === 0)
          || (typeof value === 'string' && value.trim().length === 0)
        )
      }
    }
  }
</script>
<style>
  .v-bg {
    height: 100%;
    min-height: 100vh;
    background-position: center;
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-image: url('../../assets/fullstack.jpg');
  }
  canvas {
    display: block;
    vertical-align: bottom;
  } /* ---- particles.js container ---- */
  #particles-js {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: none;
    background-image: url('');
    background-repeat: no-repeat;
    background-size: cover;
    background-position: 50% 50%;
  } /* ---- stats.js ---- */
  .count-particles {
    background: #000022;
    position: absolute;
    top: 48px;
    left: 0;
    width: 80px;
    color: #13e8e9;
    font-size: 0.8em;
    text-align: left;
    text-indent: 4px;
    line-height: 14px;
    padding-bottom: 2px;
    font-family: Helvetica, Arial, sans-serif;
    font-weight: bold;
  }
  .js-count-particles {
    font-size: 1.1em;
  }
  #stats,
  .count-particles {
    -webkit-user-select: none;
    margin-top: 5px;
    margin-left: 5px;
  }
  #stats {
    border-radius: 3px 3px 0 0;
    overflow: hidden;
  }
  .count-particles {
    border-radius: 0 0 3px 3px;
  }
</style>
