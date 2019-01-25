<template>
  <div class="app-container">
    <el-form label-width="150px" >
      <el-form-item label="指令服务器地址" style="display:none;">
        <el-input v-model="setting.AT_SERVER_URL" placeholder="请输入指令服务器URL地址"></el-input>
      </el-form-item>
      <el-form-item label="指令超时时间">
        <el-input-number v-model="setting.AT_CMD_TIMEOUT" :min="5" :max="10000"></el-input-number>&nbsp;秒
      </el-form-item>
      <el-form-item label="设备离线时间">
        <el-input-number v-model="setting.DEVICE_OFFLINE_TIME" :min="0" :max="10000"></el-input-number>&nbsp;秒
      </el-form-item>
      <el-form-item label="短信发送间隔时间">
        <el-input-number v-model="setting.SMS_SEND_INTERVAL" :min="0" :max="10000"></el-input-number>&nbsp;秒
      </el-form-item>
      <el-form-item label="设备列表刷新时间">
        <el-input-number v-model="setting.DEVICE_LIST_REFRESH" :min="0" :max="10000"></el-input-number>&nbsp;秒
      </el-form-item>
      <el-form-item label="安卓APP下载地址" style="display:none;">
        <el-input v-model="setting.APP_DOWN_URL" placeholder="请输入安卓APP下载地址"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="reset">初始化</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
      <el-alert title="说明" type="warning" description="1.以上设置的时间的单位均为秒；2.用户必须开启短信推送，上述发送时间间隔才会生效" show-icon :closable="false">
      </el-alert>
    </el-form>
  </div>
</template>
<script>
import { getSetting, saveSetting } from '@/api/device';

export default {
  data() {
    return {
      setting: {
        DEVICE_OFFLINE_TIME: 0,
        SMS_SEND_INTERVAL: 0,
        AT_SERVER_URL: '',
        AT_CMD_TIMEOUT: 0,
        APP_DOWN_URL: '',
        DEVICE_LIST_REFRESH: 0
      }
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      getSetting({}).then(response => {
        this.setting = response.data;
      })
    },
    reset() {
      this.setting = {
        DEVICE_OFFLINE_TIME: 600,
        SMS_SEND_INTERVAL: 3500,
        AT_SERVER_URL: this.setting.AT_SERVER_URL,
        AT_CMD_TIMEOUT: 60,
        APP_DOWN_URL: this.setting.APP_DOWN_URL,
        DEVICE_LIST_REFRESH: 60
      }
    },
    save() {
      saveSetting(this.setting).then(response => {
        if (response.code === 20000) {
          this.$message({ message: '保存成功', type: 'success' });
        } else {
          this.$message({ message: response.data, type: 'error' });
        }
      });
    }
  }
};
</script>