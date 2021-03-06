<template>
  <div class="content">
    <el-row>
      <el-col>
        <el-form :inline="true" :model="queryForm" ref="queryForm" class="demo-form-inline">
          <el-form-item label="状态" prop="type">
            <el-select size="small" v-model="queryForm.queryStatus" placeholder="请选择" @change="obtainValue">
              <el-option label="全部" value="all"></el-option>
              <el-option label="导入数据处理中" value="IMPORT"></el-option>
              <el-option label="导入完成" value="READY"></el-option>
              <el-option label="执行查询中" value="QUERYING"></el-option>
              <el-option label="查询完成" value="QUERYED"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" icon="el-icon-search" @click="onSubmit">查询</el-button>
            <el-button type="primary" size="small" icon="el-icon-upload" @click="importExcel">导入</el-button>
            <el-button type="primary" size="small"  @click="refresh">刷新</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <el-row>
      <el-col>
        <el-table size="small"
                  :data="tableData"
                  style="width: 100%">
          <el-table-column
                           prop="createDate"
                           label="创建日期">
          </el-table-column>
          <el-table-column
                           prop="importCount"
                           label="导入条数">
          </el-table-column>
          <el-table-column
            prop="invalidCount"
            label="不合规条数">
          </el-table-column>
          <el-table-column
                           prop="usedTimes"
                           label="使用批量执行次数">
          </el-table-column>
          <el-table-column sortab
                           prop="queryCount"
                           label="查询条数">
          </el-table-column>
          <el-table-column
            prop="invalidCount"
            label="不合规条数">
          </el-table-column>
          <el-table-column
            prop="queryStatus" :formatter="statusFormatter"
            label="状态">
          </el-table-column>
          <el-table-column
            fixed="right" width="300px"
            label="操作">
            <template slot-scope="scope" >
              <el-button type="primary" size="small"  @click="exportExcel(scope.row)">导出</el-button>
              <el-button type="primary" size="small"  @click="details(scope.row)">明细</el-button>
              <el-button type="primary" size="small"   @click="batchQuery(scope.row)">执行批量查询</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <page-nation :totalCount="totalCount" :pageCount="pageCount" v-on:pageChange="loadImport"></page-nation>
    <upload-file :centerDialogVisible="uploadDialog" :url="this.url+'/'+this.type+'/import'" @close="closeFile"></upload-file>
  </div>
</template>

<script>
  import PageNation from "../../components/PageNation"
  import UploadFile from "./UploadFile"

  export default {
    name: 'ImportCarList',
    components: {
      PageNation,
      UploadFile
    },
    data() {
      return {
        url: process.env.BASE_API,
        uploadDialog: false,
        queryForm: {
          queryStatus: "all",
          type: "all"
        },
        tableData: [],
        totalCount: 1,
        pageCount: 0,
        pageSize: 10,
        type:"car"
      };
    },
    mounted() {
      this.loadImport(1, this.pageSize)
    },
    methods: {
      obtainValue(v) {
        this.queryForm.queryStatus = v;
      },
      onSubmit() {
        this.loadImport(1, this.pageSize)
      },
      statusFormatter(row, column) {
        let status = row.queryStatus;
        if (status === 'IMPORT') {
          return '导入数据处理中'
        }
        if (status === 'READY') {
          return '导入完成'
        }
        if (status === 'QUERYING') {
          return '执行查询中'
        }

        if (status === 'QUERYED') {
          return '查询完成'
        }
      },
      reset() {
        this.$refs["queryForm"].resetFields();
      },
      refresh() {
        this.loadImport(1, this.pageSize)
      },
      details(row) {
        this.$router.push({path:'/batchQueryCars',query: {importId: row.id}})
      },
      closeFile() {
        this.loadImport(1, this.pageSize)
        this.uploadDialog = false;
      },
      batchQuery(row) {
        this.$confirm('您还有'+this.$store.getters.user.times+'批量执行次数，' +
          '一共可以处理' +this.$store.getters.user.totalCount+'条数据，'+
          '将对所有未查询的身份证进行批量查询，并且将消耗查询次数, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$request.post({
            url: '/'+this.type+'/batchQuery',
            data: {
              importId: row.id,
            },
            success: result => {
              this.loadImport(1, this.pageSize);
              this.bus.$emit("refreshUser");
              this.$message.success("执行成功");
            },
            error: e => {
              this.$message.error(e)
            }
          })
        }).catch((e) => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      exportExcel(row) {
        this.$request.download({
          url: '/'+this.type+'/export',
          data: {
            importId: row.id,
            cardNumber: this.queryForm.cardNumber,
            status: this.queryForm.type=='all'?null:this.queryForm.type,
          }
        })
      },
      importExcel() {
        this.uploadDialog = true;
      },

      loadImport(page, pageSize) {
        this.$request.post({
          url: '/'+this.type+'/loadImport',
          data: {
            queryStatus:this.queryForm.queryStatus!="all"?this.queryForm.queryStatus:null,
            type: this.type.toUpperCase(),
            page: page - 1,
            pageSize: pageSize
          },
          success: result => {
            this.tableData = result.data.items;
            this.totalCount = result.data.totalCount;
            this.pageCount = result.data.totalPage;
          },
          error: e => {
            this.$message.error(e)
          }
        })
      }
    }
  };
</script>
