<template>
  <div>
    <position :locations="locations"></position>

        <el-form :model="form" :rules="rules" ref="form" label-width="80px" size="small">
          <el-form-item label="用户名" prop="model">
            <el-input v-model="form.username"  placeholder="用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder=密码></el-input>
          </el-form-item>
          <el-form-item label="查询次数" prop="times">
            <el-input v-model="form.times" placeholder=查询次数></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="realName" >
            <el-input v-model="form.realName"  placeholder=姓名></el-input>
          </el-form-item>
          <el-form-item label="查询配置" prop="referUrl" >
            <el-input v-model="form.referUrl"  placeholder=查询配置></el-input> <i style="font-size: 12px;color: red">配置第三方查询上下文，查询时将使用，请正确配置</i>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="onSubmit">保存</el-button>
            <el-button type="info" icon="el-icon-search" @click="cancel">取消</el-button>
          </el-form-item>
        </el-form>
  </div>
</template>

<script>
  import Position from "../../components/Postion"
  // @ is an alias to /src
  export default {
    name: 'UserForm',
    components: {
      Position
    },
    data() {
      return {
        roleList:[],
        form: {
          id: null,
          username: null,
          password: null,
          times:0,
          realName:null,
          referUrl:null
        },
        rules: {
          username: [
            {required: true, message: '请输入名称', trigger: 'blur'},
            {min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur'}
          ]
        },
        locations: [
          {
            name: "用户管理",
            path: "/userList"
          },
          {
            name: "编辑用户",
          }
        ]
      };
    },
    mounted() {
      if (this.$route.params.id) {
        this.form.id=this.$route.params.id
        this.loadUser(this.$route.params.id)
      }
    },
    methods: {
      onSubmit() {
        this.$refs["form"].validate((valid) => {
          if (valid) {
            // this.fullscreenLoading = true;
            this.$request.post({
              url: '/user/save',
              data: this.form,
              success: result => {
                this.$message({
                  type: 'success',
                  message: `更新成功`
                });
                this.cancel()
              },
              error: e => {
                this.$message.error(e)
              }
            })
          }
        })
      },
      cancel() {
        this.$router.push({path: "/userList"})
      }
      ,
      loadUser(id) {
        this.$request.get({
          url: '/user/findUserById',
          config: {
            params: {
              id: id
            }
          },
          success: result => {
            this.$utils.copyFromTo(result.data, this.form);
          },
          error: e => {
            this.$message.error(e)
          }
        })
      }
    }
  };
</script>

<style scoped>

</style>
