
export function fWorkingState(state) {
  var res = state == 1 ? '休止' 
    : state == 2 ? '正常' 
    : state == 3 ? '油箱故障' 
    : state == 4 ? '低温保护' 
    : state == 5 ? '设置参数'
    : '未知-' + state;
  return res;
}

export function fProcessingState(state) {
  var res = state == 1 ? '休止' 
    : state == 2 ? '润滑状态' 
    : state == 4 ? '低温保护' 
    : state == 5 ? '设置参数'
    : '未知-' + state;
  return res;
}

export function fFuelLevelState(state) {
  var res = state == 0 ? '正常液位' 
    : state == 1 ? '预警液位' 
    : state == 2 ? '低液位' 
    : '未知-' + state;
  return res;
}

export function fFuelPressureState(state) {
  var res = state == 0 ? '正常' 
    : state == 1 ? '报警' 
    : state == 2 ? '润滑到压' 
    : state == 3 ? '润滑不到压' 
    : '未知-' + state;
  return res;
}
