# 数据库结构说明入口

数据库结构说明已统一迁移到 `docs/database-structure.md`。

请以后优先阅读和维护：

- `docs/database-structure.md`：当前数据库结构、表关系、状态值、种子数据和已知不一致。
- `backend/init.sql`：本地初始化脚本。
- `backend/src/main/java/com/group12/backend/entity/`：后端运行时实体定义。

保留本文件是为了兼容 README 和历史引用。不要在这里继续维护第二份表结构说明，避免新旧文档互相冲突。
