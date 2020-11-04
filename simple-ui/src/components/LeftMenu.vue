<template>
  <el-aside class="menu_page" :width="isCollapse ? '64px' : '200px'">
    <el-menu :default-active="defaultAction"
             class="menu-Bar"
             background-color="#343268"
             text-color="#fff"
             mode="vertical"  :collapse="isCollapse"  :collapse-transition="false"
    >
      <template v-for="item in items">
        <el-menu-item v-if="item.children ==null || item.children.length == 0" :index="item.code" v-on:click="doNavigation(item.name,item.url)">
          <i :class="item.icon"></i>
          <span slot="title">{{item.name}}</span>
        </el-menu-item>
        <!-- 判断是否有二级路由 -->
        <el-submenu v-if="item.children" :index="item.code">
          <template slot="title">
            <i :class="item.icon"></i>
            <span slot="title">{{item.name}}</span>
          </template>
          <!--递归组件，把遍历的值传回子组件，完成递归调用-->
          <Menu :items="item.children"></Menu>
        </el-submenu>
      </template>
    </el-menu>
  </el-aside>
</template>
<script>
  import Menu from "../components/Menu"

  export default {
    name: 'LeftMenu',
    components: {
      Menu
    },
    watch: {
      '$route'(to) {
        let menuName
        let menuPath
        if (!to.params || !to.params.menuName) {
          var option = this.findMenu(this.items)
          if (option) {
            menuName = option.name;
            menuPath = option.url;
            this.defaultAction = option.code;
          } else {
            this.defaultAction = null;
            return
          }
        } else {
          this.defaultAction = to.params.menuPath;
        }
        if (to.meta && to.meta.primary) {
          this.bus.$emit("newMenu", menuName, menuPath)
        }
      },
      "items"() {
        if (this.items && this.items.length > 0) {
          var option = this.findMenu(this.items)
          if (option) {
            var menuName = option.name;
            var menuPath = option.url;
            this.defaultAction = option.code;
            this.bus.$emit("newMenu", menuName, menuPath)
          }
        }
      }
    },
    props: {
      items: Array
    },
    data() {
      return {
        defaultAction: "",
        isCollapse: false
      };
    },
    created() {
      this.bus.$on('headerClick', (e) => {
        this.isCollapse = !this.isCollapse;
      });
    },
    methods: {
      doNavigation(menuName, path) {
        this.bus.$emit("tabActive",menuName,path)
      },
      findMenu(items) {
        let primary = this.$route.meta && this.$route.meta.primary
        let routerName = this.$route.name;
        if (!primary) {
          routerName = this.$route.meta.parent || "Home"
        }
        if (items) {
          for (let option of items) {
            if (routerName === option.url) {
              return option;
            }
            if (option.children) {
              return this.findMenu(option.children)
            }
          }
        }
        return null;
      }
    }
  };
</script>
<style>
  .menu-Bar:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
  }


  .el-aside {
    line-height: 100px;
    height: 100%;
    background: #343268;
  }

  .el-menu {
    border: none;
    z-index: 2000;
    width: 100%;
  }
</style>
