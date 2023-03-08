<template>
  <div id="input">
    <h1>Welcome to the compiler!</h1>
    <h3>Enter C++ code you want to compile below</h3>
    <textarea v-model="message" placeholder="Multiple lines bby"></textarea>
  </div>
  <div id="compileButton">
    <button v-on:click="compile">Compile</button>
  </div>
  <div id="output">
    <p style="white-space: pre-line">{{ output }}</p>
  </div>
</template>

<script>
import { doCompile } from "@/utils/utils.js";
export default {
  name: "CompilerComp",
  data: function () {
    return {
      message: "",
      output: "",
    };
  },
  methods: {
    async compile() {
      console.log(this.message);
      const compReq = {
        code: this.message,
      };

      const compRes = doCompile(compReq);
      compRes.then((result) => {
        this.output = result;
      });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
textarea {
  width: 600px;
  height: 300px;
}
#compileButton {
  padding-top: 10px;
  padding-left: 550px;
}
</style>
