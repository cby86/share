<template>
  <el-container class="is-vertical">
    <Header></Header>
    <el-container>
      <LeftMenu :items="menus"></LeftMenu>
      <MainContent></MainContent>
    </el-container>
  </el-container>
</template>
<script>
  import MainContent from "../components/MainContent"
  import LeftMenu from "../components/LeftMenu"
  import Header from "../components/Header"

  export default {
    name: 'Layout',
    data() {
      return {
        menus: null
      }
    },
    components: {
      MainContent,
      LeftMenu,
      Header
    },
    mounted() {
      this.loadAuthentication()
    },
    beforeRouteUpdate(to, from, next) {
      if (to.path !== '/' && !this.$store.getters.hasAuth(to.name)) {
        this.$message.error("无权限访问")
        next({
          path: from.path
        })
      } else {
        next()
      }
    },
    methods: {
      checkAuth() {
        if (this.$route.path !== '/' && !this.$store.getters.hasAuth(this.$route.name)) {
          this.$message.error("无权限访问")
          this.$router.push({
            path: "/"
          })
          return false;
        }
        return true;
      },
      loadAuthentication() {
        this.menus = [
          {
            name: "首页",
            code: "Home",
            url: "Home",
            icon:"el-icon-news"
          },
          {
            name: "批量查询",
            code: "batchQuery",
            icon:"el-icon-connection",
            children:[
              {
                name: "批量查询司机",
                code: "ImportDriverList",
                url: "ImportDriverList",
                icon:"el-icon-s-check"
              },
              {
                name: "批量查询车辆",
                code: "ImportCarList",
                url: "ImportCarList",
                icon:"el-icon-truck"
              }
            ]
          }
        ]
        this.bus.$emit("initTab")
      }
    }
  }
</script>
<style>
  .el-container {
    height: 100%;
  }

  .el-form {
    margin-left: 10px;
  }

  .el-form-item {
    margin-bottom: 10px;
  }

  .el-table td {
    height: 35px;
  }

  .el-input {
    width: 200px;
  }
</style>
