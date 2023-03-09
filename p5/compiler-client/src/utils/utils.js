import axios from "axios";

export async function doCompile(compileReq) {
  return axios
    .post("http://localhost:8080/compile", compileReq)
    .then((response) => {
      return response.data;
    })
    .catch((err) => {
      console.log("Error: " + err);
    });
}
