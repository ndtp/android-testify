"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[4594],{3905:function(e,t,n){n.d(t,{Zo:function(){return l},kt:function(){return f}});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function a(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=r.createContext({}),u=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},l=function(e){var t=u(e.components);return r.createElement(c.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,l=a(e,["components","mdxType","originalType","parentName"]),d=u(n),f=o,y=d["".concat(c,".").concat(f)]||d[f]||p[f]||i;return n?r.createElement(y,s(s({ref:t},l),{},{components:n})):r.createElement(y,s({ref:t},l))}));function f(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,s=new Array(i);s[0]=d;var a={};for(var c in t)hasOwnProperty.call(t,c)&&(a[c]=t[c]);a.originalType=e,a.mdxType="string"==typeof e?e:o,s[1]=a;for(var u=2;u<i;u++)s[u]=n[u];return r.createElement.apply(null,s)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},0:function(e,t,n){n.r(t),n.d(t,{assets:function(){return l},contentTitle:function(){return c},default:function(){return f},frontMatter:function(){return a},metadata:function(){return u},toc:function(){return p}});var r=n(7462),o=n(3366),i=(n(7294),n(3905)),s=["components"],a={sidebar_position:1},c="Android Screenshot Testing",u={unversionedId:"intro",id:"version-1.2.0-alpha01/intro",title:"Android Screenshot Testing",description:"Testify",source:"@site/versioned_docs/version-1.2.0-alpha01/intro.md",sourceDirName:".",slug:"/intro",permalink:"/android-testify/docs/1.2.0-alpha01/intro",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-1.2.0-alpha01/intro.md",tags:[],version:"1.2.0-alpha01",sidebarPosition:1,frontMatter:{sidebar_position:1},sidebar:"tutorialSidebar",next:{title:"Get Started",permalink:"/android-testify/docs/1.2.0-alpha01/category/get-started"}},l={},p=[{value:"Testify",id:"testify",level:2}],d={toc:p};function f(e){var t=e.components,n=(0,o.Z)(e,s);return(0,i.kt)("wrapper",(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"android-screenshot-testing"},"Android Screenshot Testing"),(0,i.kt)("h2",{id:"testify"},"Testify"),(0,i.kt)("p",null,"Expand your test coverage by including the View layer. Testify allows you to easily set up a variety of screenshot tests in your application. Capturing a screenshot of your view gives you a new tool for monitoring the quality of your UI experience. It's also an easy way to review changes to your UI. Once you've established a comprehensive set of screenshots for your application, you can use them as a \"visual dictionary\". In this case, a picture really is worth a thousand words; it's easy to catch unintended changes in your view rendering by watching for differences in your captured images."),(0,i.kt)("p",null,"Testify screenshot tests are built on top of ",(0,i.kt)("a",{parentName:"p",href:"https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests"},"Android Instrumentation tests")," and so integrate seamlessly with your existing test suites. You can run tests and capture screenshots from within Android Studio or using the Gradle command-line tools. Testify also works well with most Continuous Integration services. "),(0,i.kt)("p",null,"You can easily capture screenshots with different resolutions, orientations, API versions and languages by simply configuring different emulators. Testify natively supports grouping screenshot tests by device characteristics. Testify captures a bitmap of your specified View after all layout and draw calls have completed so you know that you're capturing an authentic rendering representative of what your users will see in your final product."))}f.isMDXComponent=!0}}]);