<template>
    <div sytle="font-size:" CLASS="margin">
        欢迎您:{{this.$store.getters.user.realName}},您当前查询次数还剩余{{this.$store.getters.user.times}}次！
        <p>
          如果在使用过程中遇到什么问题或有什么建议，可以在反馈中提交建议
        </p>
    </div>
</template>

<script>
  export default {
    name: 'Home',
    data() {
      return {
        form:{
          name: ''
        }
      }
    },
    created() {
    },
    mounted() {
    }
  }
</script>
<style>
  .margin {
    margin-left: 5px;
  }
</style>
