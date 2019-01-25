import fetch from '@/utils/fetch';

export function getMessageList(params) {
  return fetch({
    url: '/message/list',
    method: 'post',
    data: params
  });
}

export function readMsg(params) {
  return fetch({
    url: '/message/read',
    method: 'post',
    data: params
  });
}
