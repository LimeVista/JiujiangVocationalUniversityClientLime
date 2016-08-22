package pers.lime.jjvu.html.bean;

/**
 * <p>用于存储成绩评估报告单
 * @author Lime(李振宇)
 * <p> 2016.08.06
 * @version 1.0
 */
public class Grade {

    /**
     * 学年
     */
    protected String year,

    /**
     * 课程名称
     */
    courseName,

    /**
     * 课程性质
     */
    courseNature,

    /**
     * 课程类别
     */
    courseCategory;

    /**
     * 学时
     */
    protected int classHour;

    /**
     * 平时成绩一
     */
    protected float score_1,

    /**
     * 平时成绩二
     */
    score_2,

    /**
     * 期末成绩
     */
    finalScore,

    /**
     * 总成绩
     */
    totalScore;

    public Grade() {
        super();
    }

    /**
     * 初始化
     * @param year 学年
     * @param courseName 课程名称
     * @param courseNature 课程性质
     * @param courseCategory 课程类别
     * @param classHour 学时
     * @param score_1 平时成绩一
     * @param score_2 平时成绩二
     * @param finalScore 期末成绩
     * @param totalScore 总成绩
     */
    public Grade(String year, String courseName, String courseNature, String courseCategory, int classHour,
                 float score_1, float score_2, float finalScore, float totalScore) {
        super();
        this.year = year;
        this.courseName = courseName;
        this.courseNature = courseNature;
        this.courseCategory = courseCategory;
        this.classHour = classHour;
        this.score_1 = score_1;
        this.score_2 = score_2;
        this.finalScore = finalScore;
        this.totalScore = totalScore;
    }

    /**
     * 获得本课程学年
     * @return 返回学年
     */
    public String getYear() {
        return year;
    }

    /**
     * 获取课程名称
     * @return 返回课程名称
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 获取课程性质
     * @return 返回课程性质
     */
    public String getCourseNature() {
        return courseNature;
    }

    /**
     * 获取课程类别
     * @return 返回课程类别
     */
    public String getCourseCategory() {
        return courseCategory;
    }

    /**
     * 获取本课程学时
     * @return 返回学时
     */
    public int getClassHour() {
        return classHour;
    }

    /**
     * 获取平时成绩1
     * @return 返回平时分数1
     */
    public float getScore_1() {
        return score_1;
    }

    /**
     * 获取平时成绩2
     * @return 返回平时分数2
     */
    public float getScore_2() {
        return score_2;
    }

    /**
     * 获取期末成绩
     * @return 返回期末分数
     */
    public float getFinalScore() {
        return finalScore;
    }

    /**
     * 获取总成绩
     * @return 返回总分
     */
    public float getTotalScore() {
        return totalScore;
    }

    /**
     * 获取成绩单项目数
     * @return 返回项目数
     */
    public int getItemCount(){
       return 9;
    }


    @Override
    public String toString() {
        return "Grade [year=" + year + ", courseName=" + courseName + ", courseNature=" + courseNature
                + ", courseCategory=" + courseCategory + ", classHour=" + classHour + ", score_1=" + score_1
                + ", score_2=" + score_2 + ", finalScore=" + finalScore + ", totalScore=" + totalScore + "]";
    }

}

