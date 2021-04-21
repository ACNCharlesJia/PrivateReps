package SourceCode.GroovyPlugins

import com.navis.extension.model.persistence.DynamicHibernatingEntity;
import com.navis.framework.metafields.MetafieldIdFactory;
import com.navis.framework.persistence.HibernateApi;
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.portal.query.QueryFactory;
import com.navis.argo.business.api.GroovyApi;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

class MTLDEVInsertCUSTOM_BP_ACTIVE_TARIFF extends GroovyApi {
    public String execute(Map map) {
        String FN_NAME = "(executeInsert) " ;
        if (DEBUG_MODE) this.log(FN_NAME + "Start.");

        String stringAllData = "" ;
        stringAllData = stringAllData + "STORAGE,Y#~" + "\n";
        stringAllData = stringAllData + "REEFER,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_BAYPLAN_OPERATOR_CORRECTION#~" + "\n";
        stringAllData = stringAllData + "UNIT_CATEGORY_CHANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_CATEGORY_CHANGE_ROB#~" + "\n";
        stringAllData = stringAllData + "UNIT_CHANGE_OF_OB_CARRIER#~" + "\n";
        stringAllData = stringAllData + "UNIT_CHANGE_OF_POD#~" + "\n";
        stringAllData = stringAllData + "UNIT_CONTAINER_REJECT_BY_HAULIER#~" + "\n";
        stringAllData = stringAllData + "UNIT_CONTAINER_REJECT_BY_LIGHTER#~" + "\n";
        stringAllData = stringAllData + "UNIT_DAMAGE_REPAIR_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_DAMAGE_REPAIR_ON_CHASSIS_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_DAMAGE_REPAIR_ON_MTL_CHASSIS_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_DAMAGE_REPAIR_REJECT#~" + "\n";
        stringAllData = stringAllData + "UNIT_DATA_DOWNLOAD#~" + "\n";
        stringAllData = stringAllData + "UNIT_DELIVER#~" + "\n";
        stringAllData = stringAllData + "UNIT_DELIVERY_TO_FROM_OTHER_TERMINAL#~" + "\n";
        stringAllData = stringAllData + "UNIT_DESTINATION_CHANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_DISCH#~" + "\n";
        stringAllData = stringAllData + "UNIT_DRY_FROM_TO_ACTIVE_RF#~" + "\n";
        stringAllData = stringAllData + "UNIT_ECFA_AT_CUSTOMS_COMPOUND#~" + "\n";
        stringAllData = stringAllData + "UNIT_ECFA_INSIDE_MTL#~" + "\n";
        stringAllData = stringAllData + "UNIT_EMPTY_REPLACEMENT#~" + "\n";
        stringAllData = stringAllData + "UNIT_EX_REHANDLE_IN#~" + "\n";
        stringAllData = stringAllData + "UNIT_EX_REHANDLE_OUT#~" + "\n";
        stringAllData = stringAllData + "UNIT_EX_WITHDRAW,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_FCL_WITHDRAW_VIA_CFS#~" + "\n";
        stringAllData = stringAllData + "UNIT_FKIND_CHANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_FOLD_FLATRACK#~" + "\n";
        stringAllData = stringAllData + "UNIT_FOUND_OVERWEIGHT#~" + "\n";
        stringAllData = stringAllData + "UNIT_FUMIGATION_INSIDE_TERMINAL_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_GAS_LIGHTER_INSPECTION#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_CATEGORY_CHANGE,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_CATEGORY_CHANGE_ROB,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_CHANGE_OF_OB_CARRIER,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_CHANGE_OF_POD,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_CHANGE_ROB#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_DESTINATION_CHANGE,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_FKIND_CHANGE,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_HAZARDS_IMDG_CHANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_LINE_OPR_CHANGE,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_DESTINATION_CHANGE,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_OPR_CHANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_LATE_PORT_RESTRICT_MOVE,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_LOAD#~" + "\n";
        stringAllData = stringAllData + "UNIT_LOAD_WITHDRAW_CHARGE_OPERATOR#~" + "\n";
        stringAllData = stringAllData + "UNIT_MTI_BY_BARGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_MTO_BY_BARGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_MT_IN#~" + "\n";
        stringAllData = stringAllData + "UNIT_MT_IN_AFTER_REPAIR#~" + "\n";
        stringAllData = stringAllData + "UNIT_MT_OUT#~" + "\n";
        stringAllData = stringAllData + "UNIT_MT_OUT_REPAIR#~" + "\n";
        stringAllData = stringAllData + "UNIT_MT_OUT_UNRETURN,Y#~" + "\n";
        stringAllData = stringAllData + "UNIT_MT_REJECT_DUE_TO_OIL_STAIN#~" + "\n";
        stringAllData = stringAllData + "UNIT_MT_WITHDRAW#~" + "\n";
        stringAllData = stringAllData + "UNIT_NOMINATE_MT#~" + "\n";
        stringAllData = stringAllData + "UNIT_NOMINATE_PRECOOL_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_NOMINATE_PTI_CANCEL#~" + "\n";
        stringAllData = stringAllData + "UNIT_NOMINATE_PTI_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_NOMINATE_PTI_MALFUNCTION#~" + "\n";
        stringAllData = stringAllData + "UNIT_OH_SPREADER#~" + "\n";
        stringAllData = stringAllData + "UNIT_OUT_GATE#~" + "\n";
        stringAllData = stringAllData + "UNIT_PORT_RESTRICT_MOVE#~" + "\n";
        stringAllData = stringAllData + "UNIT_PRECOOL_CANCEL#~" + "\n";
        stringAllData = stringAllData + "UNIT_PRECOOL_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_PROPERTY_UPDATE#~" + "\n";
        stringAllData = stringAllData + "UNIT_PTI_APPOINT_CHANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_PTI_CANCEL#~" + "\n";
        stringAllData = stringAllData + "UNIT_PTI_COMPLETE#~" + "\n";
        stringAllData = stringAllData + "UNIT_PTI_MALFUNCTION#~" + "\n";
        stringAllData = stringAllData + "UNIT_PTI_MT_SHIPMENT#~" + "\n";
        stringAllData = stringAllData + "UNIT_PTI_TS_EX_MT_SHPT#~" + "\n";
        stringAllData = stringAllData + "UNIT_RECEIVE#~" + "\n";
        stringAllData = stringAllData + "UNIT_REDISCH_CHG#~" + "\n";
        stringAllData = stringAllData + "UNIT_REJECT_BY_HAULIER#~" + "\n";
        stringAllData = stringAllData + "UNIT_RELEASE_AFTER_HOLDING_INSTRUCTION_ACTION#~" + "\n";
        stringAllData = stringAllData + "UNIT_RESTOW_CHARGE_OPERATOR#~" + "\n";
        stringAllData = stringAllData + "UNIT_SA_CONTAINER_VIA_CFS#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_CHARGE_OPERATOR#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_FOR_CORRECT_NO#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_FOR_INSPECTION#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_FOR_LABEL#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_FOR_SEAL#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_FROM_CFS#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_INSIDE_TERMINAL#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_INSIDE_TERMINAL_FROM_TO#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_ON_CARRIER#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_TO_CFS#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_TO_CFS_WITHDRAW#~" + "\n";
        stringAllData = stringAllData + "UNIT_SHIFT_TO_GROUND#~" + "\n";
        stringAllData = stringAllData + "UNIT_SPECIAL_ARRANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_SPECIAL_STOW#~" + "\n";
        stringAllData = stringAllData + "UNIT_STRIP#~" + "\n";
        stringAllData = stringAllData + "UNIT_STUFF#~" + "\n";
        stringAllData = stringAllData + "UNIT_TRANSLOAD#~" + "\n";
        stringAllData = stringAllData + "UNIT_TRANSLOAD_NEW_UNIT#~" + "\n";
        stringAllData = stringAllData + "UNIT_UNDERGO_PTI_APPOINT_CHANGE#~" + "\n";
        stringAllData = stringAllData + "UNIT_WEIGH_IN_YARD#~" + "\n";
        stringAllData = stringAllData + "UNIT_WEIGH_ON_CHASSIS#~" + "\n";
        stringAllData = stringAllData + "UNIT_WEIGH_UPON_DISCH#~" + "\n";
        stringAllData = stringAllData + "VV_ALL_TYPES_OF_VEHICLE#~" + "\n";
        stringAllData = stringAllData + "VV_AUTOMOBILE_OVER_12_KG#~" + "\n";
        stringAllData = stringAllData + "VV_AUTOMOBILE_OVER_19_CBM_12KG_WHM#~" + "\n";
        stringAllData = stringAllData + "VV_AUTOMOBILE_OVER_19_CBM_5KG_WHM#~" + "\n";
        stringAllData = stringAllData + "VV_AUTOMOBILE_OVER_19_CBM_NYK#~" + "\n";
        stringAllData = stringAllData + "VV_AUTOMOBILE_OVER_22_FT#~" + "\n";
        stringAllData = stringAllData + "VV_AUTOMOBILE_UNDER_22_FT#~" + "\n";
        stringAllData = stringAllData + "VV_BERTHING_GANG_STANDBY#~" + "\n";
        stringAllData = stringAllData + "VV_BERTHING_SURCHARGE_161_249M#~" + "\n";
        stringAllData = stringAllData + "VV_BERTHING_SURCHARGE_250_294M#~" + "\n";
        stringAllData = stringAllData + "VV_BERTHING_SURCHARGE_295_324M#~" + "\n";
        stringAllData = stringAllData + "VV_BERTHING_SURCHARGE_325_349M#~" + "\n";
        stringAllData = stringAllData + "VV_BERTHING_SURCHARGE_80_160M#~" + "\n";
        stringAllData = stringAllData + "VV_BERTHING_SURCHARGE_OVER_350M#~" + "\n";
        stringAllData = stringAllData + "VV_BREAKBULK_1422_CBMUNDER_7000_KG#~" + "\n";
        stringAllData = stringAllData + "VV_BREAKBULK_BY_QUAY_CRANE#~" + "\n";
        stringAllData = stringAllData + "VV_BREAKBULK_OVER_22_CBM__7000_KG#~" + "\n";
        stringAllData = stringAllData + "VV_BREAKBULK_UNDER_14_CBM__7000_KG#~" + "\n";
        stringAllData = stringAllData + "VV_BUNDLING_OF_EMPTY_FLATRACK#~" + "\n";
        stringAllData = stringAllData + "VV_CARGO_RECONSOLIDATION#~" + "\n";
        stringAllData = stringAllData + "VV_CARGO_SORTING#~" + "\n";
        stringAllData = stringAllData + "VV_CARGO_SORTING_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_CARPENTER#~" + "\n";
        stringAllData = stringAllData + "VV_CFS_CHARGE#~" + "\n";
        stringAllData = stringAllData + "VV_CFS_FLOORS_SPACE_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_CFS_LABOURER_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_CFS_LABOURER_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_CHANGE_OF_PARTICULARS#~" + "\n";
        stringAllData = stringAllData + "VV_CHANGE_OF_SHIPMENT_FOR_DUDGRF_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_CHANGE_OF_SHIPMENT_FOR_GP_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_CHASSIS_HIRE_HOURLY_RATE#~" + "\n";
        stringAllData = stringAllData + "VV_CHASSIS_HIRE_HOURLY_RATE_FUMIGATION#~" + "\n";
        stringAllData = stringAllData + "VV_CKD_PARTS#~" + "\n";
        stringAllData = stringAllData + "VV_CLERICAL_STAFFINSPECTOR#~" + "\n";
        stringAllData = stringAllData + "VV_CLERICAL_STAFFINSPECTOR_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_CLERICAL_STAFFINSPECTOR_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_CLERICAL_STAFFINSPECTOR_DAY_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_CLERICAL_STAFFINSPECTOR_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_CLERICAL_STAFFINSPECTOR_NIGHT_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_CONTAINER_REPAIR_SPECIAL#~" + "\n";
        stringAllData = stringAllData + "VV_CONTAINER_SHUFFLING_CHARGE#~" + "\n";
        stringAllData = stringAllData + "VV_CONTAINER_TRUCKING_TO_WH_BERTHS_MTL#~" + "\n";
        stringAllData = stringAllData + "VV_CONTR_TRUCKING_TO_WH_NOT_BERTH_MTL#~" + "\n";
        stringAllData = stringAllData + "VV_CSI_DETENTION_OF_CHASSIS#~" + "\n";
        stringAllData = stringAllData + "VV_CSI_INSPECTION_OUTSIDE_TERMINAL#~" + "\n";
        stringAllData = stringAllData + "VV_CSI_INSPECTION_WITHIN_TERMINAL#~" + "\n";
        stringAllData = stringAllData + "VV_DELIVERY_TOFROM_OTHER_TERMINALS#~" + "\n";
        stringAllData = stringAllData + "VV_DELIVERY_TOFROM_T125T9#~" + "\n";
        stringAllData = stringAllData + "VV_DIGITAL_PHOTO#~" + "\n";
        stringAllData = stringAllData + "VV_DIRECT_LIGHTER_LCL_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_DIRECT_RESTOW_OF_NONCONTAINERISED#~" + "\n";
        stringAllData = stringAllData + "VV_DISMANTLEDISPOSAL_OF_WOODEN_PLATFORM#~" + "\n";
        stringAllData = stringAllData + "VV_DUDGRF_CARGO_OVERTIME#~" + "\n";
        stringAllData = stringAllData + "VV_DUDGRF_CARGO_SHIFTING#~" + "\n";
        stringAllData = stringAllData + "VV_DUDGRF_CARGO_SHIFTING_BREAKBULK#~" + "\n";
        stringAllData = stringAllData + "VV_EQUIPMENT_DRIVER#~" + "\n";
        stringAllData = stringAllData + "VV_EXPORT_AUTOMOBILE_ON_WHEELS#~" + "\n";
        stringAllData = stringAllData + "VV_EXPORT_CARGO_CFS_CHARGE#~" + "\n";
        stringAllData = stringAllData + "VV_EXPORT_CARGO_PACKING_UNPACKING#~" + "\n";
        stringAllData = stringAllData + "VV_EXPORT_DGRF_CARGO_REHANDLING#~" + "\n";
        stringAllData = stringAllData + "VV_EXPORT_LCL_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_EXPORT_LCL_CARGO_OVERTIME#~" + "\n";
        stringAllData = stringAllData + "VV_FACILITY_CHARGE_FOR_CONTAINER_20_FT#~" + "\n";
        stringAllData = stringAllData + "VV_FACILITY_CHARGE_FOR_CONTAINER_40_FT#~" + "\n";
        stringAllData = stringAllData + "VV_FACILITY_CHARGE_FOR_CONTR_FROM_OUTSIDE#~" + "\n";
        stringAllData = stringAllData + "VV_FACILITY_CHARGE_FOR_CONTR_R2#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFTER_HIRE_HEAVY_DUTY#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFTER_HIRE_HEAVY_DUTY_25000KGS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE22000KGS_CAPACITY#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE2500_KGS_CAPACITY#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE7000_KGS_CAPACITY#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_22000_KGS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_22000_KGS_INTER_TML#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_25000_KGS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_25000_KGS_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_25000_KGS_DAY_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_25000_KGS_INTER_TML#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_25000_KGS_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_25000_KGS_NIGHT_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_2500_KGS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_2500_KGS_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_2500_KGS_DAY_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_2500_KGS_INTER_TML#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_2500_KGS_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_2500_KGS_NIGHT_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_7000_KGS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_7000_KGS_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_7000_KGS_DAY_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_7000_KGS_INTER_TML#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_7000_KGS_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_FORKLIFT_TRUCK_HIRE_7000_KGS_NIGHT_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_FRESH_WATER_SUPPLY#~" + "\n";
        stringAllData = stringAllData + "VV_FRESH_WATER_SUPPLY_AT_T9#~" + "\n";
        stringAllData = stringAllData + "VV_FRONT_LOADER_MT_FTS_HIRE_WITH_OPERATOR#~" + "\n";
        stringAllData = stringAllData + "VV_FUMIGATION_DEGAS_TRUCK_2_MOVES#~" + "\n";
        stringAllData = stringAllData + "VV_GANTRY_CRANE_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIESMANLABOURER_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIESMANLABOURER_CY#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIESMANLABOURER_CY_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIESMANLABOURER_CY_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIESMANLABOURER_DAY_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIESMANLABOURER_NIGHT_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIES_LABOUR_FOREMAN_CY#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIES_LABOUR_FOREMAN_CY_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIES_LABOUR_FOREMAN_CY_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_GENERAL_DUTIES_LABOUR_FORMAN_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_GEN_DUTIES_LABOUR_FOREMAN_DAY_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_GEN_DUTIES_LABOUR_FOREMAN_NIGHT_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_GP_CARGO_HANDLING_IN_OUT#~" + "\n";
        stringAllData = stringAllData + "VV_GP_CARGO_HANDLING_OUT#~" + "\n";
        stringAllData = stringAllData + "VV_GP_CARGO_OVERTIME#~" + "\n";
        stringAllData = stringAllData + "VV_GP_CARGO_SHIFTING#~" + "\n";
        stringAllData = stringAllData + "VV_GP_CARGO_SHIFTING_BREAKBULK#~" + "\n";
        stringAllData = stringAllData + "VV_GP_CARGO_STORAGE_HALF_MONTH#~" + "\n";
        stringAllData = stringAllData + "VV_HATCH_COVER_LIFTING_ON_AND_OFF#~" + "\n";
        stringAllData = stringAllData + "VV_HEAVY_DUTY_VEHICLE_NYK#~" + "\n";
        stringAllData = stringAllData + "VV_HEAVY_DUTY_VEHICLE_WHM#~" + "\n";
        stringAllData = stringAllData + "VV_HEAVY_DUTY_VEHICLE_WHM_5KG#~" + "\n";
        stringAllData = stringAllData + "VV_HIRE_OF_CHASSIS#~" + "\n";
        stringAllData = stringAllData + "VV_HIRE_OF_CHASSIS_INTER_TML#~" + "\n";
        stringAllData = stringAllData + "VV_HIRE_OF_LORRY_25_TONS_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_HIRE_OF_LORRY_25_TONS_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_HUSTLING_CHASSIS_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_AUTOMOBILE_ON_WHEELS#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_AUTOMOBILE_ON_WHEELS_OVERTIME#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_BONDED_DG_CARGO_OVERTIME#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_CARGO_CFS_CHARGE#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_CARGO_PACKING_UNPACKING#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_DIRECT_UC_UNIT_IB#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_DUDGRF_CARGO_OVERTIME#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_DUDGRF_CARGO_OVERTIME_BREAKBULK#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_GP_CARGO_OVERTIME#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_GP_CARGO_OVERTIME_BREAKBULK#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_INDIRECT_UC_UNIT_IB#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_LCL_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_IMPORT_RFDG_CARGO_REHANDLING#~" + "\n";
        stringAllData = stringAllData + "VV_INDIRECT_LIGHTER_CARGO_SHIFTING#~" + "\n";
        stringAllData = stringAllData + "VV_INDIRECT_LIGHTER_CARGO_SORTING#~" + "\n";
        stringAllData = stringAllData + "VV_INDIRECT_RESTOW_OF_NONCONTAINERISED#~" + "\n";
        stringAllData = stringAllData + "VV_INDUSTRIAL_VEH_FORKLIFT_UNDER_5_KILO#~" + "\n";
        stringAllData = stringAllData + "VV_LABOUR_CHARGE_MEAL_HOURS#~" + "\n";
        stringAllData = stringAllData + "VV_LABOUR_GANG#~" + "\n";
        stringAllData = stringAllData + "VV_LABOUR_GANG_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_LASHINGUNLASHING_OF_UC_DISCLOAD#~" + "\n";
        stringAllData = stringAllData + "VV_LASHINGUNLASHING_OF_UC_RESTOWAGE#~" + "\n";
        stringAllData = stringAllData + "VV_LASHINGUNLASHING_OF_UC_TRANSHIPMENT#~" + "\n";
        stringAllData = stringAllData + "VV_LATE_CHANGE_OF_CONTAINER_STATUS_COS#~" + "\n";
        stringAllData = stringAllData + "VV_LIFTING_CHARGE_FOR_CONTR_FROM_OUTSIDE#~" + "\n";
        stringAllData = stringAllData + "VV_LIGHTER_FACILITY_CHARGE#~" + "\n";
        stringAllData = stringAllData + "VV_LIGHTER_FACILITY_CHARGE_HGL_BB_COLLECT#~" + "\n";
        stringAllData = stringAllData + "VV_LIGHTER_WORK_20_FT#~" + "\n";
        stringAllData = stringAllData + "VV_LIGHTER_WORK_40_FT#~" + "\n";
        stringAllData = stringAllData + "VV_LIGHTER_WORK_45_FT#~" + "\n";
        stringAllData = stringAllData + "VV_LIGHTER_WORK_BY_QUAY_CRANE#~" + "\n";
        stringAllData = stringAllData + "VV_MAFI_TRAILER_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_MONTHLY_STORAGE_OF_GP_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_MOVEMENT_CHARGE_CFS_BREAKBULK#~" + "\n";
        stringAllData = stringAllData + "VV_MOVEMENT_CHARGE_CFS_EQP#~" + "\n";
        stringAllData = stringAllData + "VV_MOVEMENT_CHARGE_CSI#~" + "\n";
        stringAllData = stringAllData + "VV_MOVEMENT_CHARGE_CY#~" + "\n";
        stringAllData = stringAllData + "VV_MOVEMENT_CHARGE_CY_EQP#~" + "\n";
        stringAllData = stringAllData + "VV_MOVEMENT_CHARGE_SHIFT_BREAKBULK#~" + "\n";
        stringAllData = stringAllData + "VV_OTHER_CARGO_SHIFTING_SPECIAL#~" + "\n";
        stringAllData = stringAllData + "VV_OTVOW_ATUNDER_19CBM_OR_12_KILOTONS#~" + "\n";
        stringAllData = stringAllData + "VV_OTVOW_ATUNDER_19CBM_OR_5_KILOTONS#~" + "\n";
        stringAllData = stringAllData + "VV_OVERHEIGHT_SPREADERADAPTOR#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_AUTOMOBILE#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_AUTOMOBILE_OVER_19_CBM_NYK#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_AUTOMOBILE_OVER_19_CBM_WHM_12KG#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_AUTOMOBILE_OVER_19_CBM_WHM_5KG#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_AUTOMOBILE_UNDER_16_FT#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_AUTOMOBILE_UNDER_22_FT#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BB#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BB_PER_UNITUNDER_14_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BB_PER_UNIT_1013_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BB_PER_UNIT_1422_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BB_PER_UNIT_OVER_22_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BB_PER_WEEKUNDER_14_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BB_UNDER_10_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BREAKBULK_14_22_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_BREAKBULK_OVER_22_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_CKD_PARTS#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_CKD_PARTS_UNDER_14_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_FORKLIFTS_UNDER_5_KILO#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_HEAVY_DUTY_VEHICLES_NYK#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_HEAVY_DUTY_VEHICLES_WHM_12KG#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_HEAVY_DUTY_VEHICLES_WHM_5KG#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_HEAVY_DUTY_VEHICLES_WHM_TRACKED#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_HEAVY_DUTY_VEH_WHM_NON-DRIVABLE#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_NONCONTAINERISED_UNIT#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_TRACKEDOTHER_VEHICLE_EUK_16FT#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_TRACKEDOTHER_VEHICLE_TOI_22FT#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_TRACKEDOTHER_VEHICLE_TOI_5KG#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_TRACKEDOTHER_VEH_EUK_SPECIAL_VEH#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_TRACKEDOTHER_VEH_TOI_TRACKED_VEH#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_VEHICLE_NORMAL_SIZE#~" + "\n";
        stringAllData = stringAllData + "VV_OVERTIME_OF_VEHICLE_NORMAL_SIZE_NYK_5T#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_20_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_20_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_40_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_40_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_40_FT_HC_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_40_FT_HC_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_45_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_45_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_STACK20_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_STACK40_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_PACKINGUNPACKING_STACK45_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_20_FT_AT_DAY_PALLETIZED#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_40_FT_AT_DAYPALLETIZED#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_40_FT_HC_AT_DAYPALLETIZED#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_45_FT_AT_DAYPALLETIZED#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_STACKING20_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_STACKING40_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_STACKING45_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_STACK_40_FT_HC_AT_DAY_#~" + "\n";
        stringAllData = stringAllData + "VV_PACKUNPACK_STACK_40_FT_HC_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_PHOTOCOPYING_CHARGE_A4FS#~" + "\n";
        stringAllData = stringAllData + "VV_PHOTOCOPYING_CHARGE_B4A3#~" + "\n";
        stringAllData = stringAllData + "VV_PLUGUNPLUG_REEFER_CONTR_ON_BOARD#~" + "\n";
        stringAllData = stringAllData + "VV_QUAYFACE_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_QUAY_CRANE_HIRE_CAR_CARRIER#~" + "\n";
        stringAllData = stringAllData + "VV_QUAY_CRANE_HIRE_CY#~" + "\n";
        stringAllData = stringAllData + "VV_QUAY_CRANE_WITH_DRIVER_HOURLY_RATE#~" + "\n";
        stringAllData = stringAllData + "VV_QUAY_CRANE_WITH_DRIVER_HOURLY_RATE_INTER_TML#~" + "\n";
        stringAllData = stringAllData + "VV_RECOVERY_OF_SECURITY_GUARD_CHARGE#~" + "\n";
        stringAllData = stringAllData + "VV_RECOVERY_TRUCKING_CHARGE#~" + "\n";
        stringAllData = stringAllData + "VV_RENTAL_OF_RTG_BLOCKSTACK#~" + "\n";
        stringAllData = stringAllData + "VV_RENTAL_OF_TUG_MASTER#~" + "\n";
        stringAllData = stringAllData + "VV_RESTOWAGE_VIA_QUAY_STACKING_FRAMES#~" + "\n";
        stringAllData = stringAllData + "VV_RESTOW_OF_SHIP_SPREADER#~" + "\n";
        stringAllData = stringAllData + "VV_RTG_HIRE_WITH_OPERATOR#~" + "\n";
        stringAllData = stringAllData + "VV_SHIFTING_DG_BBUNDER_14_CBM_7000_KG#~" + "\n";
        stringAllData = stringAllData + "VV_SHIFTING_GP_BBUNDER_14_CBM_7000_KG#~" + "\n";
        stringAllData = stringAllData + "VV_SHIFTING_LCL_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_SHIFTING_OF_BREAKBULK_OVER_14_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_SHIFTING_OF_GP_BB_ATOVER_14_CBM#~" + "\n";
        stringAllData = stringAllData + "VV_SHIP_SPREADER_DISCHARGELOAD#~" + "\n";
        stringAllData = stringAllData + "VV_SHIP_SPREADER_TRANSHIPMENT#~" + "\n";
        stringAllData = stringAllData + "VV_SORTING_MARKSUNPACKING_20_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_SORTING_MARKSUNPACKING_40_FT_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_SORTING_MARKS_UNPACKING_20_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_SORTING_MARKS_UNPACKING_40_FT_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_STORAGE_OF_USED_LASHING_EQUIPMENT#~" + "\n";
        stringAllData = stringAllData + "VV_SUPERVISORY_STAFF_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_SUPERVISORY_STAFF_CY#~" + "\n";
        stringAllData = stringAllData + "VV_SUPERVISORY_STAFF_CY_DAY_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_SUPERVISORY_STAFF_CY_NIGHT_SHIFT#~" + "\n";
        stringAllData = stringAllData + "VV_SUPERVISORY_STAFF_DAY_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_SUPERVISORY_STAFF_NIGHT_SHIFT_CFS#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_FT_WITH_DRIVER#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_FT_WITH_DRIVER_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_FT_WITH_DRIVER_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_FT_WITH_DRIVER_MEAL_HOUR#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_LABOUR_IN_LIGHTERS#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_TALLYMANLABOUR#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_TALLYMANLABOUR_AT_DAY#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_TALLYMANLABOUR_AT_NIGHT#~" + "\n";
        stringAllData = stringAllData + "VV_SUPPLY_OF_TALLYMANLABOUR_MEAL_HOUR#~" + "\n";
        stringAllData = stringAllData + "VV_SYSTEM_ENHANCEMENT_AP#~" + "\n";
        stringAllData = stringAllData + "VV_SYSTEM_ENHANCEMENT_SA#~" + "\n";
        stringAllData = stringAllData + "VV_SYSTEM_ENHANCEMENT_SSA#~" + "\n";
        stringAllData = stringAllData + "VV_TOP_LOADER_HIRE_WITH_DRIVER#~" + "\n";
        stringAllData = stringAllData + "VV_TRACKEDOTHER_VEHICLE_TOI_5KG#~" + "\n";
        stringAllData = stringAllData + "VV_TRACKEDOTHER_VEHICLE_TOI_TRACKED_VEH#~" + "\n";
        stringAllData = stringAllData + "VV_TRACKEDOTHER_VEHICLE_WHM#~" + "\n";;
        stringAllData = stringAllData + "VV_TRAILER_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_TRANSHIPMENT_LCL_CARGO#~" + "\n";
        stringAllData = stringAllData + "VV_TRANSHIPMENT_UC_NONREEFER_UNIT#~" + "\n";
        stringAllData = stringAllData + "VV_UC_AUTOMOBILES#~" + "\n";
        stringAllData = stringAllData + "VV_UC_DRIVEABLETOWABLE_VEHICLE#~" + "\n";
        stringAllData = stringAllData + "VV_UC_TRACKED_VEHICLES_#~" + "\n";
        stringAllData = stringAllData + "VV_UNDECLARE_MEASURING_OVERSIZE_CONTR#~" + "\n";
        stringAllData = stringAllData + "VV_VEHICLE_ON_WHEELS_NORMAL_SIZE_NYK_19CBM#~" + "\n";
        stringAllData = stringAllData + "VV_VEHICLE_ON_WHEELS_NORMAL_SIZE_NYK_UNDER_5TONS#~" + "\n";
        stringAllData = stringAllData + "VV_VENTSEAL_CHECKING#~" + "\n";
        stringAllData = stringAllData + "VV_VOW_ATUNDER_19_CBM_OR_12_KILOTONS#~" + "\n";
        stringAllData = stringAllData + "VV_VOW_ATUNDER_19_CBM_OR_5_KILOTONS#~" + "\n";
        stringAllData = stringAllData + "VV_WEEKLY_CARGO_STORAGE#~" + "\n";
        stringAllData = stringAllData + "VV_WEIGHBRIDGE_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_WIRE_SLING_HIRE#~" + "\n";
        stringAllData = stringAllData + "VV_YACHTCRADLE_ADD_CHARGE_DIRECT#~" + "\n";
        stringAllData = stringAllData + "VV_YACHTCRADLE_ADD_CHARGE_INDIRECT#~" + "\n";

        if (DEBUG_MODE) this.log(FN_NAME + "End.");

        return execute(stringAllData);
    }

    public String execute2() {
        String FN_NAME = ".execute ";
        this.log(FN_NAME + "Start.");

        List listError = [];
        return execute(stringAllData) ;
    }

    public String execute(String sData) {
        String FN_NAME = ".execute " ;
        this.log(FN_NAME + "Start.");

        List listError = [];

        List<String> listCustomBpActiveTariff = RawDataToList(sData) ;
        this.log(FN_NAME + "listCustomBpActiveTariff.size() :" + listCustomBpActiveTariff.size());
        this.log(FN_NAME + "listCustomBpActiveTariff :" + listCustomBpActiveTariff.toString());

        for (String item : listCustomBpActiveTariff) {
            this.log(FN_NAME + "item :" + item);
            String stringTempEventName = "";
            String stringTempConditionalBase = "";

            if (item.contains(",Y")) {
                StringTokenizer token = new StringTokenizer(item, ",");
                if (DEBUG_MODE) this.log(FN_NAME + "token : " + token.toString());

                stringTempEventName = token.nextToken();
                stringTempConditionalBase = token.nextToken();
            } else {
                stringTempEventName = item;
                stringTempConditionalBase = "";
            }

            String stringResult = "";
            stringResult = this.createCustomBpActiveTariff(stringTempEventName, stringTempConditionalBase) ;

            if(stringResult != "")
                listError.add(stringResult);
        }

        if (listError.size() > 0)
            log(FN_NAME + "ERROR :" + listError.toString());
        else
            log(FN_NAME + "RUN SUCCESSFULLY WITH RECORD CREATED:" + listCustomBpActiveTariff.size());

        log(FN_NAME + "End.");
        return new Date();
    }

    private List<String> RawDataToList(String sOriginal) {
        String FN_NAME = ".RawDataToList " ;
        log(FN_NAME + "Start.");

        log(FN_NAME + "stringAllData :" + sOriginal);

        String[] stringDataArray = sOriginal.replace("\n", "").split("#~") ;
        log(FN_NAME + "stringDataArray.size().toString() :" + stringDataArray.size().toString());

        List<String> lString = [];
        for (int iItem = 0 ; iItem < stringDataArray.size(); iItem++) {
            lString.add(stringDataArray[iItem]) ;
        }

        log(FN_NAME + "lString : " + lString.toString());
        log(FN_NAME + "End.");
        return lString ;
    }


    private Map ListToMap(List myList) {
        String FN_NAME = ".ListToMap ";
        log(FN_NAME + "Start.");

        Map<String, Map<String,String>> myMap = new HashMap<String, Map<String,String>>() ;
        Map<String, String> myDataMap = [:];
        Map<String, String> myTempDataMap = [:];

        for (int iListIndex = 0 ; iListIndex < myList.size() ; iListIndex++) {
            String stringEventName = "" ;
            String stringToMap = "" ;

            log(FN_NAME + "listCustomerGroup.get($iListIndex).toString() :" + myList.get(iListIndex).toString());
            myList.get(iListIndex).each {
                switch ( it.key ) {
                    case pfsEventName :
                        stringEventName = it.value ;
                        log(FN_NAME + it.key + " :" + stringEventName);
                        break;
                }
            }

            myTempDataMap = myMap.get(stringEventName) ;
            if (myTempDataMap == null) {
                myDataMap = new HashMap<String, String>();
                log(FN_NAME + " NEW :" + stringEventName);
                myDataMap.put(pfsEventName, stringEventName);

                myMap.put(stringEventName, myDataMap) ;
            } else {
                log(FN_NAME + " APPEND :" + stringEventName);
                String stringTempEventName = myTempDataMap.get(pfsEventName) ;
                log(FN_NAME + " APPEND :" + stringTempEventName);
                myTempDataMap.putAt(pfsChildOperatorCode, stringTempChildOperatorCode) ;
            }
        }
        log(FN_NAME + "End.");

        return myMap ;
    }

    private String createCustomBpActiveTariff( String sEventName, String sConditionalBase) {
        String FN_NAME = ".createCustomBpActiveTariff " ;
        log(FN_NAME + "Start.");

        String result = null;
        List<DynamicHibernatingEntity> dhelist = this.getCustomBpActiveTariffRecord(sEventName);

        LogDynamicHibernatingEntity(dhelist) ;

        if (dhelist.isEmpty()) {
            log(FN_NAME + "sEventName : " + sEventName);

            DynamicHibernatingEntity dhe = new DynamicHibernatingEntity(ENTITY_NAME);
            dhe.setFieldValue(MetafieldIdFactory.valueOf(fieldEventName),sEventName);
            dhe.setFieldValue(MetafieldIdFactory.valueOf(fieldConditionalBase),sConditionalBase);

            HibernateApi.getInstance().save(dhe);
            HibernateApi.getInstance().flush() ;

            log(FN_NAME + "HibernateApi.getInstance().save(dhe).toString() :" + HibernateApi.getInstance().save(dhe).toString());
        } else {
            result = "Record already exist " +
                    "sEventName:" + sEventName + "\n";
        }

        log(FN_NAME + "End.");

        return result;
    }

    private List<DynamicHibernatingEntity> getCustomBpActiveTariffRecord(String sEventName) {
        String FN_NAME = ".getCustomBpActiveTariffRecord " + ": " + sEventName + " ";
        this.log(FN_NAME + "Start.");

        DomainQuery dq = QueryFactory.createDomainQuery(ENTITY_NAME)
                .addDqPredicate(PredicateFactory.eq(MetafieldIdFactory.valueOf(fieldEventName), sEventName))
                ;

        List<DynamicHibernatingEntity> dheList = HibernateApi.getInstance().findEntitiesByDomainQuery(dq);

        log(FN_NAME + "End.");
        return dheList ;
    }

    public void logMap(Map MapToLog) {
        String FN_NAME = ".logMap " ;
        log(FN_NAME + "Start.");

        MapToLog.eachWithIndex { obj, i ->
            log(FN_NAME + "\${i}: \${obj}" + "${i}: ${obj}") ;
        }

        log(FN_NAME + "End.");
        return;
    }

    public void logList(List ListToLog) {
        String FN_NAME = ".logList ";
        log(FN_NAME + "Start.");

        ListToLog.eachWithIndex { obj, i ->
            log(FN_NAME + "\${i}: \${obj} " + "${i}: ${obj}") ;
        }

        log(FN_NAME + "End.");
        return;
    }

    public void LogDynamicHibernatingEntity(List<DynamicHibernatingEntity> list) {
        String FN_NAME = ".LogDynamicHibernatingEntity " ;
        log(FN_NAME + "Start.");

        for (int i = 0 ; i < list.size() ; i++) {
            String stringTempEventName = (String) list.get(i).getFieldValue(MetafieldIdFactory.valueOf(fieldEventName));

            log(FN_NAME + " " + stringTempEventName);
        }
        log(FN_NAME + "End.");
    }

    private String RemoveLastDelimiter(String sOriginal, String sDelimiter) {
        String FN_NAME = ".RemoveLastDelimiter " ;
        log(FN_NAME + "Start.");

        if (sOriginal == "")
            return "" ;

        String sOutput = "" ;

        int iStart = sOriginal.length() - sDelimiter.length() ;
        int iEnd = sOriginal.length() ;
        log(FN_NAME + "iStart : " + iStart.toString());
        log(FN_NAME + "iEnd : " + iEnd.toString());

        String sLastChar =  sOriginal.subSequence(iStart, iEnd);
        String sFirst =  sOriginal.subSequence(0, iStart);

        log(FN_NAME + "sFirst : " + sFirst);
        log(FN_NAME + "sLastChar : " + sLastChar);

        if (iStart > 0 && sLastChar == sDelimiter) {
            sOutput = sOriginal.subSequence(0, iStart);
        } else {
            sOutput = sOriginal;
        }
        log(FN_NAME + "sOutput : " + sOutput) ;

        log(FN_NAME + "End.");

        return sOutput ;
    }

    private final String pfsEventName = "Event Name";

    private final String fieldEventName = "customEntityFields.custombpatEventName";
    private final String fieldConditionalBase = "customEntityFields.custombpatConditionalBase";
    private final String ENTITY_NAME = "com.mtl.billing.CustomBPActiveTariff";

    private final boolean DEBUG_MODE = true;
}