<template>
  <div class="content">
    <Postion :locations="locations"></Postion>
    <el-row>
      <el-col>
        <el-form :inline="true" :model="queryForm" ref="queryForm" class="demo-form-inline">
          <el-form-item label="身份证号码" prop="cardNumber">
            <el-input size="small" v-model="queryForm.cardNumber" placeholder="身份证号码"></el-input>
          </el-form-item>
          <el-form-item label="状态" prop="type">
            <el-select size="small" v-model="queryForm.type" placeholder="请选择" @change="obtainValue">
              <el-option label="全部" value="all"></el-option>
              <el-option label="已查询" value="PROCCED"></el-option>
              <el-option label="待查询" value="WARITIN"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" icon="el-icon-search" @click="onSubmit">查询</el-button>
            <el-button type="primary" size="small" icon="el-icon-reset" @click="reset">清空</el-button>
            <el-button type="primary" size="small" icon="el-icon-upload" @click="exportExcel">导出</el-button>
            <el-button type="primary" size="small" icon="el-icon-upload" @click="batchQuery">执行批量查询</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <el-row>
      <el-col>
        <el-table size="small"
                  :data="tableData"
                  style="width: 100%">
          <el-table-column sortable
                           prop="cardNumber"
                           label="身份证号码">
          </el-table-column>
          <el-table-column sortable
                           prop="createDate"
                           label="创建日期">
          </el-table-column>
          <el-table-column sortable
                           prop="driverName"
                           label="司机姓名">
          </el-table-column>
          <el-table-column sortable
                           prop="mobile"
                           label="联系电话">
          </el-table-column>
          <el-table-column sortable
                           prop="area"
                           label="发证地区">
          </el-table-column>
          <el-table-column sortable
                           prop="name"
                           label="从业类别">
          </el-table-column>
          <el-table-column sortable
                           prop="fullName"
                           label="从业类别全称">
          </el-table-column>
          <el-table-column sortable
                           prop="firstDate"
                           label="初领日期">
          </el-table-column>
          <el-table-column sortable
                           prop="beginDate"
                           label="有效期起">
          </el-table-column>
          <el-table-column sortable
                           prop="endDate"
                           label="截至日期">
          </el-table-column>
          <el-table-column sortable
                           prop="company"
                           label="企业">
          </el-table-column>
          <el-table-column sortable
                           prop="status" :formatter="statusFormatter"
                           label="状态">
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <page-nation :totalCount="totalCount" :pageCount="pageCount" v-on:pageChange="loadDrivers"></page-nation>
    <upload-file :centerDialogVisible="uploadDialog" url="/api/driver/import" @close="closeFile"></upload-file>
  </div>
</template>

<script>
  import PageNation from "../../components/PageNation"
  import Postion from "../../components/Postion"
  import UploadFile from "./UploadFile"

  export default {
    name: 'BatchQueryDrivers',
    components: {
      PageNation,
      UploadFile,
      Postion
    },
    data() {
      return {
        importId: null,
        uploadDialog: false,
        queryForm: {
          cardNumber: null,
          type: "all"
        },
        tableData: [],
        totalCount: 1,
        pageCount: 0,
        pageSize: 10,
        locations: [
          {
            name: "批量执行记录",
            path: "/importDriver"
          },
          {
            name: "司机明细",
          }
        ]
      };
    },
    mounted() {
      this.importId = this.$route.query.importId;
      if (!this.importId) {
        this.$router.push('/')
        return
      }
      this.loadDrivers(1, this.pageSize)
    },
    methods: {
      statusFormatter(row, column) {
        let status = row.status;
        if (status === 'WARITIN') {
          return '待查询'
        } else {
          return '已查询'
        }
      },
      closeFile() {
        this.loadDrivers(1, this.pageSize)
        this.uploadDialog = false;
      },
      reset() {
        this.$refs["queryForm"].resetFields();
      },
      obtainValue(v) {
        this.queryForm.type = v;
      },
      onSubmit() {
        this.loadDrivers(1, this.pageSize)
      },
      batchQuery() {
        this.$confirm('您还有' + this.$store.getters.user.times + '批量执行次数，' +
          '一共可以处理' + this.$store.getters.user.times * 100 + '条数据，' +
          '将对所有未查询的身份证进行批量查询，并且将消耗查询次数, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$request.post({
            url: '/driver/batchQuery',
            data: {
              importId: this.importId
            },
            success: result => {
              this.loadDrivers(1, this.pageSize)
              this.bus.$emit("refreshUser")
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
      exportExcel() {
        this.$request.download({
          url: '/driver/export',
          data: {
            importId: this.importId,
            cardNumber: this.queryForm.cardNumber,
            status: this.queryForm.type == 'all' ? null : this.queryForm.type,
          }
        })
      },
      importExcel() {
        this.uploadDialog = true;
      },
      loadDrivers(page, pageSize) {
        this.$request.post({
          url: '/driver/loadDrivers',
          data: {
            cardNumber: this.queryForm.cardNumber,
            importId: this.importId,
            status: this.queryForm.type == 'all' ? null : this.queryForm.type,
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
