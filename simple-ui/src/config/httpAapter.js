import axios from './httpConfig'
import {Loading, Message} from 'element-ui'
import Qs from 'qs'

var request = {}

request.get = function ({url, config = {}, success, error}) {
  axios.get(url, config).then((res) => {
    successHand(res, success)
  }).catch((e) => {
    errorHand(e, error);
  });
};

request.post = function ({url, data, config = {headers: {'Content-Type': 'application/x-www-form-urlencoded'}}, success, error}) {
  axios.post(url, data ? Qs.stringify(data, {allowDots: true}) : null, config).then(
    (res) => {
      successHand(res, success)
    }).catch((e) => {
    errorHand(e, error)
  });
};

request.download = function ({url, data, config = {responseType: 'blob'},error}) {
  axios.post(url, data ? Qs.stringify(data, {allowDots: true}) : null, config).then(
    (res) => {
      download(res)
    }).catch((e) => {
    errorHand(e, error)
  });
};

function download(res) {

  let blob = res.data
  let reader = new FileReader()
  reader.readAsDataURL(blob)
  reader.onload = (e) => {
    let a = document.createElement('a')
    a.download = decodeURIComponent(res.headers.filename)
    a.href = e.target.result
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
  }
}


function errorHand(e, error) {
  if (error) {
    error(e)
  }
  else {
    Message.error({message: e.message || "网络错误"});
  }
}

function successHand(res, success) {
  if (res.status === 200) {
    let result = res.data;
    if (success) {
      success(result);
    }
  }
}

export default request;
