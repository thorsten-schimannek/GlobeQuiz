package com.shnoop.globequiz.gamedata;

public class QuestionFindArea extends Question {

    private String m_asset_file;

    public QuestionFindArea(String questionText, int typeIndex, int dataIndex, String continent,
                            String assetFile, double longitude, double latitude,
                            double boundingDiameter, int experience){

        super(questionText, typeIndex, dataIndex, continent, QuestionType.Mode.FIND_LOCATION_AREA,
                longitude, latitude, boundingDiameter, experience);

        m_asset_file = assetFile;
    }

    public String getAssetFile() { return m_asset_file; }
}
