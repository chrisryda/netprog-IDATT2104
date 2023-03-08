<template>
  <CompilerComp />
</template>

<script>
import CompilerComp from "./components/CompilerComp.vue";

export default {
  name: "App",
  components: {
    CompilerComp,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
