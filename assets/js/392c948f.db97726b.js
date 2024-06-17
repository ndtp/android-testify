"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[7671],{3905:(e,t,a)=>{a.d(t,{Zo:()=>u,kt:()=>y});var n=a(7294);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function o(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function i(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?o(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):o(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function s(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},o=Object.keys(e);for(n=0;n<o.length;n++)a=o[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)a=o[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var l=n.createContext({}),c=function(e){var t=n.useContext(l),a=t;return e&&(a="function"==typeof e?e(t):i(i({},t),e)),a},u=function(e){var t=c(e.components);return n.createElement(l.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,o=e.originalType,l=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),p=c(a),m=r,y=p["".concat(l,".").concat(m)]||p[m]||d[m]||o;return a?n.createElement(y,i(i({ref:t},u),{},{components:a})):n.createElement(y,i({ref:t},u))}));function y(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=a.length,i=new Array(o);i[0]=m;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[p]="string"==typeof e?e:r,i[1]=s;for(var c=2;c<o;c++)i[c]=a[c];return n.createElement.apply(null,i)}return n.createElement.apply(null,a)}m.displayName="MDXCreateElement"},7385:(e,t,a)=>{a.d(t,{Z:()=>o});var n=a(7294);function r(){return r=Object.assign?Object.assign.bind():function(e){for(var t=1;t<arguments.length;t++){var a=arguments[t];for(var n in a)Object.prototype.hasOwnProperty.call(a,n)&&(e[n]=a[n])}return e},r.apply(this,arguments)}const o=e=>{let{title:t,titleId:a,...o}=e;return n.createElement("svg",r({xmlns:"http://www.w3.org/2000/svg",width:16,height:16,viewBox:"0 0 16 16","aria-labelledby":a},o),t?n.createElement("title",{id:a},t):null,n.createElement("path",{style:{stroke:"none",fillRule:"nonzero",fill:"#9e80b5",fillOpacity:1},d:"M3 14a.947.947 0 0 1-.7-.3.947.947 0 0 1-.3-.7V3c0-.266.098-.5.3-.7.2-.202.434-.3.7-.3h4.652v1H3v10h10V8.348h1V13c0 .266-.098.5-.3.7-.2.202-.434.3-.7.3Zm3.367-3.652-.699-.715L12.301 3H8.652V2H14v5.348h-1V3.715Zm0 0"}))}},5162:(e,t,a)=>{a.d(t,{Z:()=>i});var n=a(7294),r=a(6010);const o={tabItem:"tabItem_Ymn6"};function i(e){let{children:t,hidden:a,className:i}=e;return n.createElement("div",{role:"tabpanel",className:(0,r.Z)(o.tabItem,i),hidden:a},t)}},5488:(e,t,a)=>{a.d(t,{Z:()=>d});var n=a(7462),r=a(7294),o=a(6010),i=a(2389),s=a(7392),l=a(7094),c=a(2466);const u={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function p(e){const{lazy:t,block:a,defaultValue:i,values:p,groupId:d,className:m}=e,y=r.Children.map(e.children,(e=>{if((0,r.isValidElement)(e)&&"value"in e.props)return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)})),f=p??y.map((e=>{let{props:{value:t,label:a,attributes:n}}=e;return{value:t,label:a,attributes:n}})),b=(0,s.l)(f,((e,t)=>e.value===t.value));if(b.length>0)throw new Error(`Docusaurus error: Duplicate values "${b.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`);const h=null===i?i:i??y.find((e=>e.props.default))?.props.value??y[0].props.value;if(null!==h&&!f.some((e=>e.value===h)))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${h}" but none of its children has the corresponding value. Available values are: ${f.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);const{tabGroupChoices:v,setTabGroupChoices:g}=(0,l.U)(),[w,k]=(0,r.useState)(h),T=[],{blockElementScrollPositionUntilNextRender:N}=(0,c.o5)();if(null!=d){const e=v[d];null!=e&&e!==w&&f.some((t=>t.value===e))&&k(e)}const O=e=>{const t=e.currentTarget,a=T.indexOf(t),n=f[a].value;n!==w&&(N(t),k(n),null!=d&&g(d,String(n)))},j=e=>{let t=null;switch(e.key){case"ArrowRight":{const a=T.indexOf(e.currentTarget)+1;t=T[a]??T[0];break}case"ArrowLeft":{const a=T.indexOf(e.currentTarget)-1;t=T[a]??T[T.length-1];break}}t?.focus()};return r.createElement("div",{className:(0,o.Z)("tabs-container",u.tabList)},r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,o.Z)("tabs",{"tabs--block":a},m)},f.map((e=>{let{value:t,label:a,attributes:i}=e;return r.createElement("li",(0,n.Z)({role:"tab",tabIndex:w===t?0:-1,"aria-selected":w===t,key:t,ref:e=>T.push(e),onKeyDown:j,onFocus:O,onClick:O},i,{className:(0,o.Z)("tabs__item",u.tabItem,i?.className,{"tabs__item--active":w===t})}),a??t)}))),t?(0,r.cloneElement)(y.filter((e=>e.props.value===w))[0],{className:"margin-top--md"}):r.createElement("div",{className:"margin-top--md"},y.map(((e,t)=>(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==w})))))}function d(e){const t=(0,i.Z)();return r.createElement(p,(0,n.Z)({key:String(t)},e))}},5989:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>p,contentTitle:()=>c,default:()=>f,frontMatter:()=>l,metadata:()=>u,toc:()=>d});var n=a(7462),r=(a(7294),a(3905)),o=a(5488),i=a(5162),s=a(7385);const l={},c="Using @TestifyLayout with library projects",u={unversionedId:"recipes/testify-layout-library",id:"recipes/testify-layout-library",title:"Using @TestifyLayout with library projects",description:"The TestifyLayout annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.",source:"@site/docs/recipes/5-testify-layout-library.md",sourceDirName:"recipes",slug:"/recipes/testify-layout-library",permalink:"/android-testify/docs/recipes/testify-layout-library",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/recipes/5-testify-layout-library.md",tags:[],version:"current",sidebarPosition:5,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Increase the matching tolerance",permalink:"/android-testify/docs/recipes/tolerance"},next:{title:"Passing Intent extras to the Activity under test",permalink:"/android-testify/docs/recipes/intents"}},p={},d=[],m={toc:d},y="wrapper";function f(e){let{components:t,...a}=e;return(0,r.kt)(y,(0,n.Z)({},m,a,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"using-testifylayout-with-library-projects"},"Using @TestifyLayout with library projects"),(0,r.kt)("p",null,"The ",(0,r.kt)("inlineCode",{parentName:"p"},"TestifyLayout")," annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.\nUnfortunately ",(0,r.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/android/R"},(0,r.kt)("inlineCode",{parentName:"a"},"R")," ",(0,r.kt)(s.Z,{mdxType:"OpenNew"})),' fields are not constants in Android library projects and R.layout resource IDs cannot be used as annotations parameters.\nInstead, you can specify a fully qualified resource name of the form "package:type/entry" as the ',(0,r.kt)("inlineCode",{parentName:"p"},"layoutResName")," argument on ",(0,r.kt)("inlineCode",{parentName:"p"},"TestifyLayout"),"."),(0,r.kt)("div",{className:"admonition admonition-caution alert alert--warning"},(0,r.kt)("div",{parentName:"div",className:"admonition-heading"},(0,r.kt)("h5",{parentName:"div"},(0,r.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,r.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"16",height:"16",viewBox:"0 0 16 16"},(0,r.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M8.893 1.5c-.183-.31-.52-.5-.887-.5s-.703.19-.886.5L.138 13.499a.98.98 0 0 0 0 1.001c.193.31.53.501.886.501h13.964c.367 0 .704-.19.877-.5a1.03 1.03 0 0 0 .01-1.002L8.893 1.5zm.133 11.497H6.987v-2.003h2.039v2.003zm0-3.004H6.987V5.987h2.039v4.006z"}))),"caution")),(0,r.kt)("div",{parentName:"div",className:"admonition-content"},(0,r.kt)("p",{parentName:"div"},"Use of this parameter is discouraged because resource reflection makes it harder to perform build optimizations and compile-time verification of code. It is much more efficient to retrieve resources by identifier (e.g. ",(0,r.kt)("inlineCode",{parentName:"p"},"@TestifyLayout(layoutId = R.foo.bar)"),") than by name (e.g. ",(0,r.kt)("inlineCode",{parentName:"p"},'@TestifyLayout(layoutResName = "package:foo/bar")'),")."),(0,r.kt)("p",{parentName:"div"},"This annotation parameter is provided to allow for loading of resources from Library projects, which may otherwise be impossible."))),(0,r.kt)(o.Z,{mdxType:"Tabs"},(0,r.kt)(i.Z,{value:"rule",label:"ScreenshotTestRule",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MainActivityScreenshotTest {\n\n    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)\n\n    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule.assertSame()\n    }\n}\n'))),(0,r.kt)(i.Z,{value:"scenario",label:"ScreenshotScenarioRule",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MainActivityScreenshotTest {\n\n    @get:Rule var rule = ScreenshotScenarioRule()\n\n    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        launchActivity<MainActivity>().use { scenario ->\n            rule\n                .withScenario(scenario)\n                .assertSame()\n        }\n    }\n}\n')))))}f.isMDXComponent=!0}}]);