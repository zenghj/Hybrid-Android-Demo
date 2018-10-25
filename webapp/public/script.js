var $sayHelloBtn = $('#btn');
var $titleInput = $('#title');
var $nativeAvailable = $('.native-available');
var $btnToChangeTitle = $('#btnToChangeTitle');
var isDebug = false;
var JAVA_TO_JS = window.JAVA_TO_JS;
window.JS_TO_JAVA = {
  cleatTitleInput: function() {
    $titleInput.val('');
  },
  say: function(str) {
    alert(str);
  }
};
// Object.assign(window, JS_TO_JAVA);

if(!JAVA_TO_JS) {
  isDebug = true;
  console.log('H5 Debug Mode');
  JAVA_TO_JS = {
    setTitle: function(str) {
      console.log('JAVA_TO_JS.setTitle', str);
    }
  }
}

$sayHelloBtn.click(function() {
  alert("global say invoked from h5");
});

if(JAVA_TO_JS) {
  $nativeAvailable.show();
} else {
  alert("native not available")
}

$btnToChangeTitle.click(function() {
  var titleVal = $titleInput.val();
  if(titleVal.trim() !== '') {
    JAVA_TO_JS.setTitle(titleVal);
  } else {
    alert("请先输入title内容");
  }
})

