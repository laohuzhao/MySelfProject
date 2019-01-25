<template>
  <div class="app-container">
    <el-tabs v-model="activeName1">
      <el-tab-pane label="基本信息" name="base_info" style="padding-top:25px;">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="grid-content bg-purple" style="float:right;padding-right:35px;display:none;">
            </div>
          </el-col>
          <el-col :span="16">
            <div class="grid-content bg-purple" style="padding-left:200px;">
              <el-form label-width="100px">
                <el-form-item label="账号名称">
                  {{info.name}}
                </el-form-item>
                <el-form-item label="所属角色">
                  {{info.role_name}}
                </el-form-item>
                <el-form-item label="手机号码">
                  {{info.mobile}}
                </el-form-item>
              </el-form>
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
    <el-tabs v-model="activeName2">
      <el-tab-pane label="个性设置" name="setting" style="padding-left:200px;padding-top:25px;">
        <el-form label-width="100px">
          <el-form-item label="是否开启推送">
            <el-switch v-model="info.push_on" on-text="" off-text=""></el-switch>
            &nbsp;&nbsp;<em>(往客户手机推送报警信息)</em>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="save">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import PanThumb from '@/components/PanThumb';
import { getSetting, saveSetting } from '@/api/setting';

export default {
  components: { PanThumb },
  data() {
    return {
      activeName1: 'base_info',
      activeName2: 'setting',
      value1: false,
      info: {
        name: '',
        role_name: '',
        corp_name: '',
        mobile: '',
        push_on: false
      }
    };
  },
  created() {
    getSetting({}).then(response => {
      if (response.code === 20000) {
        this.info.name = response.data.name;
        this.info.role_name = response.data.role_name;
        this.info.corp_name = response.data.corp_name;
        this.info.mobile = response.data.mobile;
        this.info.push_on = response.data.sms_push === 0 ? false : true;
      }
    });
  },
  methods: {
    save() {
      saveSetting({ sms_push: this.info.push_on ? 1 : 0 }).then(response => {
        if (response.code === 20000) {
          this.$message({ message: '保存成功', type: 'success' });
        } else {
          this.$message({ message: response.data, type: 'error' });
        }
      }, error => {
      });
    }
  }
};
</script>
