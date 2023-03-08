import axios from "axios";

export async function doCompile(compileReq) {
  return axios
    .post(`http://localhost:8080/compile`, compileReq, {
      auth: {
        code: compileReq.code,
      },
    })
    .then((response) => {
      return response.data;
    });
}
