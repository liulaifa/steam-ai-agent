# steam-ai-agent
# AI 面馆助手

传统收银前台 + AI 智能助手，实现智能点餐推荐与交互式对话服务。

项目已部署上线：https://www.u3562592.nyat.app:23059

## 📖 项目背景

在传统餐饮场景中，顾客点餐依赖菜单翻阅或服务员推荐，缺乏个性化体验；商家也难以通过智能化手段提升点餐效率与客单价。本项目将大模型能力引入收银前台，通过智能对话与推荐，帮助顾客快速决策、商家提升运营效率。

## 🎯 核心功能

| 模块 | 功能说明 |
|------|----------|
| **智能点餐推荐** | 基于大模型理解用户需求，推荐个性化菜品组合 |
| **对话式交互** | 自然语言对话完成点餐、查询菜品、下单等操作 |
| **菜单查询** | 缓存热门菜品数据，支持快速查询与展示 |
| **订单管理** | 创建、查询、认领订单，支持购买/出售双向交易 |
| **RAG 语义检索** | 基于向量检索的菜品语义搜索，提升推荐相关性 |

## 🛠️ 技术栈

### 后端

| 技术 | 用途 |
|------|------|
| Spring Boot 3.2 | 应用框架 |
| Spring Cloud Gateway | API 网关 |
| Nacos | 服务注册与配置管理 |
| Spring AI | AI 框架集成 |
| 阿里云百炼（qwen-plus） | 大模型服务 |
| qwen-max（向量模型） | 文本向量化 |
| MyBatis-Plus | ORM 框架 |
| MySQL | 主数据库 |
| Redis | 缓存 + 向量存储 |
| WebSocket | 实时通信 |

### 前端

| 技术 | 用途 |
|------|------|
| Vue 3 | 前端框架 |
| Element Plus | UI 组件库 |
| Vite | 构建工具 |
| Axios | HTTP 请求 |

### 部署

| 技术 | 用途 |
|------|------|
| Docker | 容器化 |
| Docker Compose | 容器编排 |
| Nginx | 静态资源托管 + 反向代理 |

## 🏗️ 系统架构
用户浏览器
↓
Nginx（前端静态资源 + 反向代理）
↓
Spring Cloud Gateway（统一路由 + 鉴权）
↓
├── AI 服务（大模型对话 + function calling）
├── 订单服务（购买/出售订单）
├── 用户服务（用户信息）
├── 文件服务（文件管理）
└── 日志服务（操作日志）
↓
MySQL（主数据） + Redis（缓存/向量存储）
↓
阿里云百炼（qwen-plus 大模型 + qwen-max 向量模型）

text

## 🚀 快速开始

### 前置条件

- JDK 17
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose
- 阿里云百炼 API Key

### 本地开发

1. 克隆项目

```bash
git clone https://github.com/your-username/steam-ai-agent.git
cd steam-ai-agent
配置 API Key

在 application.yml 中配置阿里云百炼 API Key：

yaml
spring:
  ai:
    dashscope:
      api-key: your-api-key
启动 MySQL 和 Redis

bash
docker-compose up -d mysql redis
启动后端

在 IDE 中运行 GatewayApplication

启动前端

bash
cd frontend
npm install
npm run dev
访问

text
http://localhost:5173
Docker 部署
bash
docker-compose up -d
📁 项目结构
text
steam-ai-agent/
├── backend/                    # 后端服务（8个微服务模块）
│   ├── gateway/                # API 网关
│   ├── ai-service/             # AI 对话服务
│   ├── order-service/          # 订单服务
│   ├── user-service/           # 用户服务
│   ├── file-service/           # 文件服务
│   ├── log-service/            # 日志服务
│   ├── common/                 # 公共模块
│   └── Dockerfile
├── frontend/                   # 前端应用
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── api/                # API 请求
│   │   └── utils/              # 工具函数
│   ├── dist/                   # 打包产物
│   └── Dockerfile
└── docker-compose.yml          # 容器编排
🤖 AI 能力集成
1. 大模型接入
基于 Spring AI 框架接入阿里云百炼大模型（qwen-plus），通过 @ChatClient 注解完成对话交互。

java
@ChatClient
private final ChatClient chatClient;

public String chat(String userMessage) {
    return chatClient.prompt()
        .user(userMessage)
        .call()
        .content();
}
2. Function Calling
实现 function calling 工具调用，AI 可主动调用业务接口完成点餐、下单等操作。

java
@Tool(name = "createOrder", description = "创建订单")
public String createOrder(OrderRequest request) {
    // 调用订单服务创建订单
    return orderService.create(request);
}
3. RAG 检索增强生成
结合向量模型（qwen-max）与向量数据库（Redis），实现菜品语义检索。用户询问模糊需求时，先通过向量检索召回相关菜品，再交由大模型生成推荐。

text
用户输入："适合冬天吃的暖身菜"
    ↓
向量检索 → 召回相关菜品（羊肉汤、炖牛腩...）
    ↓
大模型生成 → 推荐菜品 + 搭配建议
📊 数据库设计
核心表：

表名	说明
user	用户表
buy_order / sell_order	购买/出售订单表
product	菜品表
order_detail	订单明细表
📝 项目收获
掌握 Spring AI 框架与大模型接入的完整链路

实践 Function Calling 工具调用，打通 AI 与业务系统闭环

完成 RAG 检索增强生成方案落地，了解向量检索与大模型结合的基本范式

独立完成 8 个微服务模块的架构设计与开发

积累微服务拆分、服务注册发现、网关路由等分布式系统开发经验
