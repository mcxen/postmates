# PostMates - 外卖后台管理
项目基于 Spring boot, Mybatis plus, JDBC, Spring MVC等框架，搭建类似于PostMates的的外卖后台模块。


![截屏2023-05-16 19.55.10](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/截屏2023-05-16%2019.55.10.png)

## 实现效果

### 验证权限，跳转回登录页面：



### 禁用，修改，添加信息

![截屏2023-05-16 19.55.56](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-05-16%2019.55.56.png)

### 图片上传与下载：



![截屏2023-05-16 19.54.26](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-05-16%2019.54.26.png)



后台代码：

```java
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

//    这个是读取的yml文件的属性，就是参数为static的upload的位置
    @Value("${takeout-food.images}")
    private String imagesUrl;

    /**
     * @param file 上传的文件
     * @Description: 文件上传
     */
    @PostMapping("/upload")
    //在Spring框架中，MultipartFile接口常用于处理文件上传相关的操作。
    public R<String> upload(MultipartFile file) {
        log.info("已经接收到文件，文件名为：" + imagesUrl + file.getOriginalFilename());
        // 获取文件扩展名
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        log.info(extension);
        // 创建指定目录的文件对象
        File dir = new File(imagesUrl);
        // 判断目录是否存在
        if (!dir.exists()) {
            // 不存在时，应该创建对应的目录，这里使用的是mkdirs方法，一次性创建多级目录。
            dir.mkdirs();
        }
        // 生成文件名
        String uuidFileName = UUID.randomUUID().toString();
        // 这里拼接完整的文件名
        String fileName = imagesUrl + uuidFileName + extension;
        // 构建文件对象
        File image = new File(fileName);
        // 此时目录已经存在，将文件转存到该地址
        try {
            file.transferTo(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(uuidFileName + extension);
    }

    /**
     * @param name 文件名称
     * @Description: 文件下载回显给页面
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //
            FileInputStream fileInputStream = new FileInputStream(new File(imagesUrl + name));

            //输出流
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
```
