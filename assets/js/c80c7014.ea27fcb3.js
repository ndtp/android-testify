"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[1201],{3905:function(e,r,n){n.d(r,{Zo:function(){return d},kt:function(){return p}});var t=n(7294);function o(e,r,n){return r in e?Object.defineProperty(e,r,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[r]=n,e}function a(e,r){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);r&&(t=t.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),n.push.apply(n,t)}return n}function i(e){for(var r=1;r<arguments.length;r++){var n=null!=arguments[r]?arguments[r]:{};r%2?a(Object(n),!0).forEach((function(r){o(e,r,n[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(n,r))}))}return e}function c(e,r){if(null==e)return{};var n,t,o=function(e,r){if(null==e)return{};var n,t,o={},a=Object.keys(e);for(t=0;t<a.length;t++)n=a[t],r.indexOf(n)>=0||(o[n]=e[n]);return o}(e,r);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(t=0;t<a.length;t++)n=a[t],r.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var s=t.createContext({}),u=function(e){var r=t.useContext(s),n=r;return e&&(n="function"==typeof e?e(r):i(i({},r),e)),n},d=function(e){var r=u(e.components);return t.createElement(s.Provider,{value:r},e.children)},f={inlineCode:"code",wrapper:function(e){var r=e.children;return t.createElement(t.Fragment,{},r)}},l=t.forwardRef((function(e,r){var n=e.components,o=e.mdxType,a=e.originalType,s=e.parentName,d=c(e,["components","mdxType","originalType","parentName"]),l=u(n),p=o,m=l["".concat(s,".").concat(p)]||l[p]||f[p]||a;return n?t.createElement(m,i(i({ref:r},d),{},{components:n})):t.createElement(m,i({ref:r},d))}));function p(e,r){var n=arguments,o=r&&r.mdxType;if("string"==typeof e||o){var a=n.length,i=new Array(a);i[0]=l;var c={};for(var s in r)hasOwnProperty.call(r,s)&&(c[s]=r[s]);c.originalType=e,c.mdxType="string"==typeof e?e:o,i[1]=c;for(var u=2;u<a;u++)i[u]=n[u];return t.createElement.apply(null,i)}return t.createElement.apply(null,n)}l.displayName="MDXCreateElement"},9432:function(e,r,n){n.r(r),n.d(r,{assets:function(){return d},contentTitle:function(){return s},default:function(){return p},frontMatter:function(){return c},metadata:function(){return u},toc:function(){return f}});var t=n(7462),o=n(3366),a=(n(7294),n(3905)),i=["components"],c={},s="Force software rendering",u={unversionedId:"recipes/software-rendering",id:"version-1.2.0-alpha01/recipes/software-rendering",title:"Force software rendering",description:"In some instances it may be desirable to use the software renderer, not Android's default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering.",source:"@site/versioned_docs/version-1.2.0-alpha01/recipes/13-software-rendering.md",sourceDirName:"recipes",slug:"/recipes/software-rendering",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/software-rendering",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-1.2.0-alpha01/recipes/13-software-rendering.md",tags:[],version:"1.2.0-alpha01",sidebarPosition:13,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Selecting an alternative capture method",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/capture-method"},next:{title:"Excluding a region from the comparison",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/exclude-regions"}},d={},f=[],l={toc:f};function p(e){var r=e.components,n=(0,o.Z)(e,i);return(0,a.kt)("wrapper",(0,t.Z)({},l,n,{components:r,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"force-software-rendering"},"Force software rendering"),(0,a.kt)("p",null,"In some instances it may be desirable to use the software renderer, not Android's default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering."),(0,a.kt)("p",null,"Please read more about ",(0,a.kt)("a",{parentName:"p",href:"https://developer.android.com/guide/topics/graphics/hardware-accel.html"},"Hardware acceleration")," for more information."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .setUseSoftwareRenderer(true)\n            .assertSame()\n    }\n")))}p.isMDXComponent=!0}}]);