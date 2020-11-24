package com.shnoop.globequiz.gamedata;

public class QuestionFindArea extends Question {

    private String m_asset_file;

    public QuestionFindArea(String questionText, int typeIndex, int dataIndex, int regionIndex,
                            int questionIndex, String assetFile, double longitude, double latitude,
                            double boundingDiameter, int experience){

        super(questionText, typeIndex, dataIndex, regionIndex, questionIndex,
                QuestionType.Mode.FIND_LOCATION_AREA, longitude, latitude, boundingDiameter, experience);

        m_asset_file = assetFile;
    }

    public String getAssetFile() { return m_asset_file; }
}
