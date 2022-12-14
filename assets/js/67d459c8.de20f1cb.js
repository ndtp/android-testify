"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[3453],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>f});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function c(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var s=n.createContext({}),p=function(e){var t=n.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},u=function(e){var t=p(e.components);return n.createElement(s.Provider,{value:t},e.children)},l="mdxType",m={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},d=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,s=e.parentName,u=c(e,["components","mdxType","originalType","parentName"]),l=p(r),d=o,f=l["".concat(s,".").concat(d)]||l[d]||m[d]||a;return r?n.createElement(f,i(i({ref:t},u),{},{components:r})):n.createElement(f,i({ref:t},u))}));function f(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=d;var c={};for(var s in t)hasOwnProperty.call(t,s)&&(c[s]=t[s]);c.originalType=e,c[l]="string"==typeof e?e:o,i[1]=c;for(var p=2;p<a;p++)i[p]=r[p];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}d.displayName="MDXCreateElement"},2248:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>s,contentTitle:()=>i,default:()=>l,frontMatter:()=>a,metadata:()=>c,toc:()=>p});var n=r(7462),o=(r(7294),r(3905));const a={},i="Providing a custom comparison method",c={unversionedId:"recipes/custom-capture",id:"recipes/custom-capture",title:"Providing a custom comparison method",description:"You can customize the algorithm used to compare the captured bitmap against the baseline by using the setCompareMethod() on ScreenshotRule.",source:"@site/docs/recipes/17-custom-capture.md",sourceDirName:"recipes",slug:"/recipes/custom-capture",permalink:"/android-testify/docs/recipes/custom-capture",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/recipes/17-custom-capture.md",tags:[],version:"current",sidebarPosition:17,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Customizing the captured bitmap",permalink:"/android-testify/docs/recipes/custom-bitmap"},next:{title:"Migrate to Testify 2.0",permalink:"/android-testify/docs/migration"}},s={},p=[],u={toc:p};function l(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"providing-a-custom-comparison-method"},"Providing a custom comparison method"),(0,o.kt)("p",null,"You can customize the algorithm used to compare the captured bitmap against the baseline by using the ",(0,o.kt)("inlineCode",{parentName:"p"},"setCompareMethod()")," on ",(0,o.kt)("inlineCode",{parentName:"p"},"ScreenshotRule"),"."),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"setCompareMethod()")," is provided with the baseline and current bitmaps and expects a boolean result. If you return true, the bitmaps are\nconsidered to be the same and the comparison succeeds. If you return false, the bitmaps are considered different and the comparison fails."),(0,o.kt)("p",null,"You can use this to define a variety of custom comparison algorithms such as allowing a tolerance of excluding certain values from the comparison."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"@ScreenshotInstrumentation\n@Test\nfun ignoreDifferences() {\n    rule\n        .setViewModifications {\n            // Set background to a random color\n            val color = \"#${Integer.toHexString(Random.nextInt(0, 16581375)).padStart(6, '0')}\"\n            it.setBackgroundColor(Color.parseColor(color))\n        }\n        .setCompareMethod { _, _ -> true }\n        .assertSame()\n}\n")))}l.isMDXComponent=!0}}]);