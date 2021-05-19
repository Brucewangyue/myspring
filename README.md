## 手写 spring 源码


#### 情景： 第一次提交前 没有在.gitignore 中声明，.idea，之后声明不起作用
```shell script
// 1清除缓存
git rm -r --cached .
// 2将要忽略的文件 添加到 .gitignore中
 
// 3添加文件
git add .
// 4再次提交
git commit -m 'update .gitignore'
git push origin master
```