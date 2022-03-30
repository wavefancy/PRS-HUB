<template>
  <div class="modal fade" id="modalInstallApp"  ref="modalInstallApp"
    tabindex="-1" aria-labelledby="modalInstallApp" aria-hidden="true" >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PopModal",
  props:['showParameters','showParModal'],
  data() {
    return {
      parameters:[],
    };
  },
  methods: {
    chooseParameter(id) {
      this.parameters.forEach((parameter) => {
        if (parameter.id === id) {
          parameter.isDisabled = !parameter.isDisabled;
        } else {
          parameter.isDisabled = true;
          parameter.val = null;
        }
      });
    },
    //校验输入的数据
    checkVal(e, id) {
      let val = e.target._value;
      this.parameters.forEach((parameter) => {
        if (parameter.id === id) {
          if ("number" === parameter.valType) {
            // 数字验证！
            var reg = new RegExp("^[0-9]*$");
            var regF = new RegExp(
              "^(([0-9]+.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*.[0-9]+)|([0-9]*[1-9][0-9]*))$"
            );
            // 修改截止时间不能为空！
            if (val != null) {
              if (!reg.test(val) && !regF.test(val)) {
                parameter.errorFlage = true;
              } else {
                parameter.errorFlage = false;
              }
            }
          }
        }
      });
    },
  },
};
</script>

<style>
    .input-group {
    padding: 0;
    }
    .form-group {
    margin-top: 1rem;
    }
    .errorMsg {
    color: red;
    }
    .note {
    font-size: 0.5rem;
    }
    label {
    margin-top: 0.2rem !important;
    }
    .input-group {
    margin-top: 0.2rem !important;
    }
</style>