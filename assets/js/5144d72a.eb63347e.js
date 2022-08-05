"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[3266],{3905:function(e,t,n){n.d(t,{Zo:function(){return d},kt:function(){return f}});var r=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var l=r.createContext({}),u=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},d=function(e){var t=u(e.components);return r.createElement(l.Provider,{value:t},e.children)},c={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},p=r.forwardRef((function(e,t){var n=e.components,i=e.mdxType,o=e.originalType,l=e.parentName,d=s(e,["components","mdxType","originalType","parentName"]),p=u(n),f=i,m=p["".concat(l,".").concat(f)]||p[f]||c[f]||o;return n?r.createElement(m,a(a({ref:t},d),{},{components:n})):r.createElement(m,a({ref:t},d))}));function f(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var o=n.length,a=new Array(o);a[0]=p;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:i,a[1]=s;for(var u=2;u<o;u++)a[u]=n[u];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}p.displayName="MDXCreateElement"},6996:function(e,t,n){n.r(t),n.d(t,{assets:function(){return d},contentTitle:function(){return l},default:function(){return f},frontMatter:function(){return s},metadata:function(){return u},toc:function(){return c}});var r=n(7462),i=n(3366),o=(n(7294),n(3905)),a=["components"],s={},l="Install and use the Android Studio Plugin",u={unversionedId:"get-started/set-up-intellij-plugin",id:"version-1.2.0-alpha01/get-started/set-up-intellij-plugin",title:"Install and use the Android Studio Plugin",description:"Testify screenshot tests are built on top of Android Instrumentation tests and so already integrate seamlessly with existing test suites. Screenshots can be captured directly from within Android Studio or using the Gradle command-line tools.",source:"@site/versioned_docs/version-1.2.0-alpha01/get-started/7-set-up-intellij-plugin.md",sourceDirName:"get-started",slug:"/get-started/set-up-intellij-plugin",permalink:"/android-testify/docs/1.2.0-alpha01/get-started/set-up-intellij-plugin",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-1.2.0-alpha01/get-started/7-set-up-intellij-plugin.md",tags:[],version:"1.2.0-alpha01",sidebarPosition:7,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Verify the tests",permalink:"/android-testify/docs/1.2.0-alpha01/get-started/verify-tests"},next:{title:"Extensions",permalink:"/android-testify/docs/1.2.0-alpha01/category/extensions"}},d={},c=[],p={toc:c};function f(e){var t=e.components,n=(0,i.Z)(e,a);return(0,o.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"install-and-use-the-android-studio-plugin"},"Install and use the Android Studio Plugin"),(0,o.kt)("img",{width:"720px",src:"../../img/screenshot_19166.jpg"}),(0,o.kt)("p",null,"Testify screenshot tests are built on top of Android Instrumentation tests and so already integrate seamlessly with existing test suites. Screenshots can be captured directly from within Android Studio or using the Gradle command-line tools."),(0,o.kt)("p",null,"Android Studio support relies on the fact that Testify tests extend ",(0,o.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/androidx/test/rule/ActivityTestRule"},"ActivityTestRule")," and can be invoked using the built-in support for running instrumentation tests with various commands (notably sidebar icons) in Android Studio. With the installation of the Intellij-platform plugin, many common Testify actions can be seamlessly integrated into your IDE. The Testify Android Studio plugin is available for Android Studio version 4.0 through Dolphin (2021.3.1 Beta 1) via the Intellij Marketplace."),(0,o.kt)("p",null,"This plugin will enhance the developer experience by adding fully integrated IDE UI for all relevant Testify commands:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"Run the Testify screenshot tests"),(0,o.kt)("li",{parentName:"ul"},"Record a new baseline image"),(0,o.kt)("li",{parentName:"ul"},"Pull screenshots from the device and into your project"),(0,o.kt)("li",{parentName:"ul"},"Remove any existing screenshot test images from the device"),(0,o.kt)("li",{parentName:"ul"},"Reveal the baseline image in Android Studio"),(0,o.kt)("li",{parentName:"ul"},"Delete the baseline image from your project")),(0,o.kt)("a",{href:"https://plugins.jetbrains.com/plugin/19166-android-testify--screenshot-instrumentation-tests"},(0,o.kt)("img",{width:"300px",alt:"Get from Marketplace",src:"../../img/get.png"})))}f.isMDXComponent=!0}}]);