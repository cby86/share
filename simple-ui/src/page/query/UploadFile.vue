<template>
  <el-dialog
    title="文件上传"
    :visible="centerDialogVisible"
    width="50%"
     @close="closeD">
    <el-upload
      class="upload-demo" :headers="importHeaders"
      ref="upload"
      :action="url"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :on-success="handleSuccess"
      :on-error="handleError"
      :limit="1"
      accept=".xls,.xlsx"
      :auto-upload="false">
      <div slot="tip" class="el-upload__tip" style="color: red">只能上传excel文件，且不超过500kb,单次导入不能超过10000行数据</div>
      <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
      <el-button type="primary" size="small" @click="download">下载模板</el-button>
    </el-upload>
    <div slot="footer" class="dialog-footer">
      <el-button size="small"  @click="closeD">关闭</el-button>
      <el-button size="small" type="primary" @click="submitUpload">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  export default {
    name: 'UploadFile',
    props: {
      centerDialogVisible:{
        default: false
      },
      url:{
        default:null
      }
    },
    data() {
      return {
        importHeaders:{token: sessionStorage.getItem('token')}
      };
    }
    ,
    mounted() {
    }
    ,
    methods: {
      handleError(err,file,fileList) {
        this.$message.error(JSON.parse(err.message).message)
      },
      handleSuccess() {
        this.$message.success("导入成功")
        this.$emit("close")
      },
      closeD(){
        this.$emit("close")
      },
      download(){
        this.$request.download({
          url: 'file/download'
        })
      },
      submitUpload() {
        this.$refs.upload.submit();
      },
      handleRemove(file, fileList) {
        console.log(file, fileList);
      },
      handlePreview(file) {
        console.log(file);
      }
    }
  };
</script>
<style>
  .el-breadcrumb{
    margin-bottom: 25px;
    margin-left: 10px;
  }
</style>
